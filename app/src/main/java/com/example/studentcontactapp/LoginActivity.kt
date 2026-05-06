package com.example.studentcontactapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.UserEntity
import com.example.studentcontactapp.databinding.ActivityLoginBinding
import com.example.studentcontactapp.utils.PrefManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PrefManager
    private val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        if (prefManager.isLoggedIn() && prefManager.isRememberMe()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek login admin default
            if (username == "admin" && password == "123456") {
                performLogin("admin", "Administrator")
                Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = database.userDao().getUserByUsername(username)
                
                if (user != null) {
                    if (user.password == password) {
                        performLogin(username, user.fullName)
                    } else {
                        Toast.makeText(this@LoginActivity, "Password salah!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "User tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(username: String, fullName: String) {
        prefManager.setLogin(true)
        prefManager.setUsername(username)
        prefManager.setFullName(fullName)
        prefManager.setRememberMe(binding.cbRememberMe.isChecked)

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}
