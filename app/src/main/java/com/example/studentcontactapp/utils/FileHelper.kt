package com.example.studentcontactapp.utils

import android.content.Context
import java.io.File

object FileHelper {
    private fun getFile(context: Context, studentNim: String): File {
        return File(context.filesDir, "note_$studentNim.txt")
    }

    fun saveNote(context: Context, studentNim: String, content: String): Boolean {
        return try {
            val file = getFile(context, studentNim)
            file.writeText(content)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadNote(context: Context, studentNim: String): String {
        return try {
            val file = getFile(context, studentNim)
            if (file.exists()) {
                file.readText()
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun deleteNote(context: Context, studentNim: String): Boolean {
        return try {
            val file = getFile(context, studentNim)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isNoteExists(context: Context, studentNim: String): Boolean {
        return getFile(context, studentNim).exists()
    }

    fun getFileSize(context: Context, studentNim: String): Long {
        val file = getFile(context, studentNim)
        return if (file.exists()) file.length() else 0L
    }
}
