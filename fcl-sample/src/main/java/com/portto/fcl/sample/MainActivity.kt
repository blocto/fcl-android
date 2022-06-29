package com.portto.fcl.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.portto.fcl.FCL
import com.portto.fcl.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setUpUi()

        setUpConfig()
    }

    private fun ActivityMainBinding.setUpUi() {
        authBtnGroup.apply {
            btnDisplayWalletProviders.setOnClickListener {
                lifecycleScope.launch {
                    FCL.discoverWallets()
                }
            }
        }
        txCard.tvScript.text = SCRIPT
    }

    private fun setUpConfig() {
        FCL.config(
            appName = "FCL sample",
            appIconUrl = "https://i.imgur.com/972JZGj.png",
            accessNode = "https://rest-testnet.onflow.org"
        )

        Timber.d("FCL - config: ${FCL.config}")
        Timber.d("FCL - config: ${FCL.config.put("woo", "A")}")
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