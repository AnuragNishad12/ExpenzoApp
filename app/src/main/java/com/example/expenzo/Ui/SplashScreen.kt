package com.example.expenzo.Ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.expenzo.MainActivity
import com.example.expenzo.R
import com.example.expenzo.Utils.BeautifulCircularProgressBar
import com.example.expenzo.ViewModel.CheckUserViewModel
import com.example.expenzo.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var viewModel: CheckUserViewModel
    private lateinit var circularProgressBar: BeautifulCircularProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        circularProgressBar = findViewById(R.id.circularProgressBar)
        viewModel = ViewModelProvider(this)[CheckUserViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        circularProgressBar.setMaxProgress(100f)
        circularProgressBar.setStrokeWidth(20f)
        circularProgressBar.setProgress(0f, animate = false)

        circularProgressBar.postDelayed({
            circularProgressBar.setProgress(30f, animate = true)
        }, 100)


        viewModel.checkUserDatainDataStore(this);

        viewModel.navigateToSignUpScreen.observe(this) { response ->
            if (response == true) {
                circularProgressBar.setProgress(100f, animate = true)
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
                finish()
            }
        }

        viewModel.createUserResult.observe(this) { response ->
            response?.let {
                if (it.status) {
                    circularProgressBar.setProgress(100f, animate = true)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                circularProgressBar.setProgress(100f, animate = true)
            }
        }


    }
}