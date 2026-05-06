package com.example.studentcontactapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.FragmentAddEditBinding
import kotlinx.coroutines.launch

class AddEditFragment : Fragment() {
    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditFragmentArgs by navArgs()
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private var isEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()

        if (args.studentId != -1) {
            isEditMode = true
            binding.tvTitle.text = "Edit Mahasiswa"
            loadStudentData(args.studentId)
        }

        binding.btnSave.setOnClickListener {
            saveStudent()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSpinner() {
        val prodiList = listOf("Pilih Prodi", "T. Informatika", "Sistem Informasi", "T. Elektro", "T. Industri")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, prodiList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProdi.adapter = adapter
    }

    private fun loadStudentData(id: Int) {
        lifecycleScope.launch {
            val student = database.studentDao().getStudentById(id)
            student?.let {
                binding.etName.setText(it.name)
                binding.etNim.setText(it.nim)
                binding.etEmail.setText(it.email)
                binding.etSemester.setText(it.semester.toString())
                
                val adapter = binding.spinnerProdi.adapter as ArrayAdapter<String>
                val position = adapter.getPosition(it.prodi)
                if (position >= 0) binding.spinnerProdi.setSelection(position)
            }
        }
    }

    private fun saveStudent() {
        val name = binding.etName.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val prodi = binding.spinnerProdi.selectedItem.toString()
        val email = binding.etEmail.text.toString().trim()
        val semesterStr = binding.etSemester.text.toString().trim()

        if (name.isEmpty() || nim.isEmpty() || prodi == "Pilih Prodi" || email.isEmpty() || semesterStr.isEmpty()) {
            Toast.makeText(requireContext(), "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val semester = semesterStr.toIntOrNull() ?: 0
        val student = StudentEntity(
            id = if (isEditMode) args.studentId else 0,
            name = name,
            nim = nim,
            prodi = prodi,
            email = email,
            semester = semester
        )

        lifecycleScope.launch {
            if (isEditMode) {
                database.studentDao().update(student)
                Toast.makeText(requireContext(), "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
            } else {
                database.studentDao().insert(student)
                Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
