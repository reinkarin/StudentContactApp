package com.example.studentcontactapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.databinding.FragmentDetailBinding
import com.example.studentcontactapp.utils.FileHelper
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private var studentNim: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val student = database.studentDao().getStudentById(args.studentId)
            student?.let {
                studentNim = it.nim
                binding.tvName.text = it.name
                binding.tvNimProdi.text = "${it.nim} · ${it.prodi}"
                binding.tvInitials.text = it.name.take(2).uppercase()
                loadNote()
            }
        }

        binding.btnSaveNote.setOnClickListener {
            if (studentNim.isNotEmpty()) {
                val content = binding.etNote.text.toString()
                if (FileHelper.saveNote(requireContext(), studentNim, content)) {
                    updateStatus()
                }
            }
        }

        binding.btnLoadNote.setOnClickListener {
            loadNote()
        }
    }

    private fun loadNote() {
        if (studentNim.isNotEmpty()) {
            val note = FileHelper.loadNote(requireContext(), studentNim)
            binding.etNote.setText(note)
            updateStatus()
        }
    }

    private fun updateStatus() {
        if (FileHelper.isNoteExists(requireContext(), studentNim)) {
            val size = FileHelper.getFileSize(requireContext(), studentNim)
            binding.tvStatus.text = "✓ Tersimpan ($size bytes)"
            binding.tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            binding.tvStatus.text = "Belum ada catatan"
            binding.tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
