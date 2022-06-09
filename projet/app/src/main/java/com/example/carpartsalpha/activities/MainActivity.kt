package com.example.carpartsalpha.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivityLoginBinding
import com.example.carpartsalpha.databinding.ActivityMainBinding
import com.example.carpartsalpha.outils.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        // Create an instance of Android SharedPreferences
        val sharedPreferences =
            getSharedPreferences(Constants.CARPARTS_PREFERENCES, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        // Set the result to the tv_main.
        binding.tvMain.text= "The logged in user is $username."
        // END
    }
}