package ru.kkaysheva.flowexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.kkaysheva.flowexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DemoDatabase
    private var userCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addUser.setOnClickListener {
            addUser()
        }

        binding.deleteLastUser.setOnClickListener {
            deleteLastUser()
        }

        database = DemoDatabase.getInstance(applicationContext)

        addUsers()
        observeUsers()
    }

    private fun addUser() {
        lifecycleScope.launch {
            database.userDao().insertUser(
                User(userName = "Name_$userCount")
            )
            userCount++
        }
    }

    private fun deleteLastUser() {
        lifecycleScope.launch {
            val lastUser: User? = database.userDao().getLastUser()
            lastUser?.let { user->
                database.userDao().deleteUser(user)
                userCount--
            }
        }
    }

    private fun addUsers() {
        lifecycleScope.launch {
            database.userDao().deleteAllUsers()
            repeat(userCount) {
                database.userDao().insertUser(
                    User(userName = "Name_$it")
                )
            }
        }
    }

    private fun observeUsers() {
        lifecycleScope.launch {
            database.userDao().getAllUsers().collect { users ->
                binding.textView.text = users.toString()
            }
        }
    }
}
