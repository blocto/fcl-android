package com.portto.fcl.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.portto.fcl.FCL
import com.portto.fcl.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTest.text = FCL.test
    }
}