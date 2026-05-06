package com.example.studentcontactapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcontactapp.R
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.FragmentHomeBinding
import com.example.studentcontactapp.utils.PrefManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StudentAdapter
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private lateinit var prefManager: PrefManager
    private var isAdmin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())
        isAdmin = prefManager.getUsername() == "admin"
        
        val fullName = prefManager.getFullName() ?: "User"
        val firstName = fullName.split(" ").firstOrNull() ?: fullName
        binding.tvWelcome.text = "Welcome, $firstName!"

        // Sembunyikan tombol tambah jika bukan admin
        binding.fabAdd.visibility = if (isAdmin) View.VISIBLE else View.GONE

        setupRecyclerView()
        
        // Hanya aktifkan swipe to delete jika admin
        if (isAdmin) {
            setupSwipeToDelete()
        }

        lifecycleScope.launch {
            database.studentDao().getAllStudents().collect { students ->
                if (students.isEmpty()) {
                    insertSampleData(fullName)
                } else {
                    adapter.submitList(students)
                }
            }
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addEditFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            isAdmin = isAdmin,
            onEditClick = { student ->
                val action = HomeFragmentDirections.actionHomeFragmentToAddEditFragment(student.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { student ->
                showDeleteConfirmation(student)
            },
            onItemClick = { student ->
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(student.id)
                findNavController().navigate(action)
            }
        )
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val student = adapter.currentList[position]
                
                lifecycleScope.launch {
                    database.studentDao().deleteById(student.id)
                    Snackbar.make(binding.root, "${student.name} dihapus", Snackbar.LENGTH_LONG)
                        .setAction("Urungkan") {
                            lifecycleScope.launch {
                                database.studentDao().insert(student)
                            }
                        }.show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvStudents)
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

    private suspend fun insertSampleData(currentUserFullName: String) {
        val sampleStudents = mutableListOf(
            StudentEntity(name = currentUserFullName, nim = "2024000", prodi = "T. Informatika", email = "${currentUserFullName.lowercase().replace(" ", "")}@mail.com", semester = 1),
            StudentEntity(name = "Ahmad Fauzi", nim = "2024001", prodi = "T. Informatika", email = "ahmad@mail.com", semester = 3),
            StudentEntity(name = "Budi Santoso", nim = "2024002", prodi = "Sistem Informasi", email = "budi@mail.com", semester = 5),
            StudentEntity(name = "Clara Wijaya", nim = "2024003", prodi = "T. Informatika", email = "clara@mail.com", semester = 1)
        )
        database.studentDao().insertAll(sampleStudents)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
