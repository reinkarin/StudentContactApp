package com.example.studentcontactapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcontactapp.R
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.ItemStudentBinding

class StudentAdapter(
    private val isAdmin: Boolean,
    private val onEditClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (StudentEntity) -> Unit,
    private val onItemClick: (StudentEntity) -> Unit
) : ListAdapter<StudentEntity, StudentAdapter.StudentViewHolder>(DiffCallback) {

    private val colors = listOf(
        R.color.blue_avatar,
        R.color.pink_avatar,
        R.color.green_avatar
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student, position)
    }

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(student: StudentEntity, position: Int) {
            binding.tvName.text = student.name
            binding.tvNim.text = student.nim
            
            // Inisial otomatis
            val initials = student.name.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()
            
            binding.tvInitials.text = initials
            
            // Warna avatar bergantian
            val colorRes = colors[position % colors.size]
            binding.tvInitials.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, colorRes)

            // Hanya tampilkan tombol jika admin
            if (isAdmin) {
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
            } else {
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }

            binding.btnEdit.setOnClickListener { onEditClick(student) }
            binding.btnDelete.setOnClickListener { onDeleteClick(student) }
            binding.root.setOnClickListener { onItemClick(student) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<StudentEntity>() {
        override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
            return oldItem == newItem
        }
    }
}
