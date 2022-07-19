package com.portto.fcl.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.portto.fcl.FCL
import com.portto.fcl.config.AppInfo
import com.portto.fcl.config.ConfigOption
import com.portto.fcl.config.NetworkEnv
import com.portto.fcl.provider.Blocto
import com.portto.fcl.provider.Dapper
import com.portto.fcl.sample.databinding.ActivityMainBinding
import com.portto.fcl.ui.discovery.showConnectWalletDialog
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
        lifecycleOwner = this@MainActivity

        with(authCard) {
            btnConnectWallet.setOnClickListener {
                showConnectWalletDialog(FCL.config.supportedWallets) {
                    lifecycleScope.launch {
                        FCL.config.put(ConfigOption.SelectedWalletProvider(it))
                        FCL.authenticate()
                        tvAddress.text = FCL.currentUser?.address.orEmpty()
                    }
                }
            }
        }

        with(authCard) {
//            btnConnectWallet.setOnClickListener { FCL.authenticate() }
        }

        with(txCard) {
            tvScript.text = SCRIPT
        }
    }

    private fun setUpConfig() {
        FCL.init(
            env = NetworkEnv.TESTNET,
            appInfo = AppInfo(),
            supportedWallets = listOf(Blocto, Dapper)
        )
        Timber.d("env: ${FCL.config.env}")
        Timber.d("app: ${FCL.config.appInfo}")
        Timber.d("wallets: ${FCL.config.supportedWallets}")
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