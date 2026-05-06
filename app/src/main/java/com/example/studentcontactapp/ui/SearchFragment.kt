package com.example.studentcontactapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.FragmentSearchBinding
import com.example.studentcontactapp.utils.PrefManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StudentAdapter
    private lateinit var prefManager: PrefManager
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())
        setupRecyclerView()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300)
                    val keyword = s.toString()
                    if (keyword.isEmpty()) {
                        database.studentDao().getAllStudents().collect {
                            adapter.submitList(it)
                        }
                    } else {
                        database.studentDao().searchStudents("%$keyword%").collect {
                            adapter.submitList(it)
                        }
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            database.studentDao().getAllStudents().collect {
                adapter.submitList(it)
            }
        }
    }

    private fun setupRecyclerView() {
        val isAdmin = prefManager.getUsername() == "admin"
        adapter = StudentAdapter(
            isAdmin = isAdmin,
            onEditClick = { student ->
                val action = SearchFragmentDirections.actionSearchFragmentToAddEditFragment(student.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { student ->
                showDeleteConfirmation(student)
            },
            onItemClick = { student ->
                val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(student.id)
                findNavController().navigate(action)
            }
        )
        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearch.adapter = adapter
    }

    private fun showDeleteConfirmation(student: StudentEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data?")
            .setMessage("Hapus \"${student.name}\"? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    database.studentDao().deleteById(student.id)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
