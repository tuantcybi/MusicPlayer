package com.tuann.musicPlayer.ui.favourite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuann.musicPlayer.R
import com.tuann.musicPlayer.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnFA.setOnClickListener {
            finish()
        }
    }
}