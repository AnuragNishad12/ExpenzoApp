package com.example.expenzo.Ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expenzo.Model.Users
import com.example.expenzo.R
import com.example.expenzo.Utils.BeautifulCircularProgressBar
import com.example.expenzo.Utils.UserDataStore
import com.example.expenzo.ViewModel.UserViewModel
import com.example.expenzo.databinding.ActivitySignUpBinding
import kotlinx.coroutines.launch

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var circularProgressBar: BeautifulCircularProgressBar
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        circularProgressBar = findViewById(R.id.circularProgressBar)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]


        circularProgressBar.setMaxProgress(100f)
        circularProgressBar.setStrokeWidth(20f)


        hideProgressBar()

        binding.btnSignUp.setOnClickListener {
            showProgressBar()
            circularProgressBar.setProgress(0f, animate = false)

            circularProgressBar.postDelayed({
                circularProgressBar.setProgress(30f, animate = true)
            }, 100)

            val uniqueName = binding.uniqueName.text.toString()

            // Save uniqueName to DataStore
            val userDataStore = UserDataStore(this)
            lifecycleScope.launch {
                userDataStore.saveUniqueName(uniqueName)
            }

            val users = Users(
                fullName = binding.fullName.text.toString(),
                email = binding.emailaddress.text.toString(),
                password = binding.password.text.toString(),
                uniqueName = uniqueName,
                mobileNumber = binding.number.text.toString(),
            )

            viewModel.createUser(users)
        }


        observeViewModel()
    }

    private fun showProgressBar() {

        findViewById<View>(R.id.mainContent).visibility = View.GONE

        findViewById<View>(R.id.progressContainer).visibility = View.VISIBLE
    }

    private fun hideProgressBar() {

        findViewById<View>(R.id.mainContent).visibility = View.VISIBLE

        findViewById<View>(R.id.progressContainer).visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.createUserResult.observe(this) { response ->

            circularProgressBar.setProgress(100f, animate = true)


            circularProgressBar.postDelayed({
                hideProgressBar()
            }, 500)

            if (response != null && response.status) {
                Toast.makeText(this, "Success: ${response.message}", Toast.LENGTH_SHORT).show()
                // Navigate to next activity or finish
                // Example: startActivity(Intent(this, MainActivity::class.java))
                // finish()
            } else {
                Toast.makeText(this, "Sign up failed!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->

            circularProgressBar.setProgress(100f, animate = true)


            circularProgressBar.postDelayed({
                hideProgressBar()
            }, 500)

            Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            Log.d("ErrorInSignUp", "error: $error")
        }
    }
}