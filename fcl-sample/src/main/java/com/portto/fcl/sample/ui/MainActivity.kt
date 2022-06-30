package com.portto.fcl.sample.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.portto.fcl.FCL
import com.portto.fcl.sample.MainViewModel
import com.portto.fcl.sample.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setUpUi()

        setUpConfig()
    }

    private fun ActivityMainBinding.setUpUi() {
        mainViewModel = viewModel
        lifecycleOwner = this@MainActivity

        with(walletDiscoveryCard) {
            btnGetWalletProviders.setOnClickListener { viewModel.getWalletProviders() }
        }

        with(txCard) {
            tvScript.text = SCRIPT
        }


        with(viewModel) {
            fclWallets.observe(this@MainActivity) {
            }
        }
    }

    private fun setUpConfig() {
        FCL.config(
            appName = "FCL sample",
            appIconUrl = "https://i.imgur.com/972JZGj.png",
            accessNode = "https://rest-testnet.onflow.org"
        )
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