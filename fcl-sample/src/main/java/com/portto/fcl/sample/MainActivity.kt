package com.portto.fcl.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.portto.fcl.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            txCard.tvScript.text = SCRIPT
        }
    }

    companion object {
        const val SCRIPT = """
transaction {
    execute {
        log("A transaction happened")
    }
}
        """
    }
}