package com.example.androidfinal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.navigation.fragment.NavHostFragment
import com.example.androidfinal.R
import com.example.androidfinal.databinding.ActivityMainBinding
import com.example.androidfinal.ui.intro.IntroFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        val navController = navHost.navController

        navController.navigate(R.id.mainFragment)

        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if (!getBoolean(IntroFragment.PREFERENCES_NAME, false)) {
                navController.navigate(R.id.introFragment)
            } else {
                navController.navigate(R.id.mainFragment)
            }
        }
    }
}