package com.portto.fcl.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.portto.fcl.Fcl
import com.portto.fcl.config.AppInfo
import com.portto.fcl.config.NetworkEnv
import com.portto.fcl.provider.blocto.Blocto
import com.portto.fcl.provider.dapper.Dapper
import com.portto.fcl.sample.R
import com.portto.fcl.sample.databinding.ActivityMainBinding
import com.portto.fcl.sample.util.*
import com.portto.fcl.ui.discovery.showConnectWalletDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModel<MainViewModel>()

    private fun requireAddress() =
        Fcl.currentUser?.address ?: throw Exception("This operation requires authentication")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Fcl.init(
            env = NetworkEnv.TESTNET,
            appInfo = AppInfo(),
            supportedWallets = listOf(
                Blocto.getInstance(bloctoAppId = BLOCTO_APP_ID, isDebug = true),
                Dapper,
            )
        )

        binding.setUpUi()
    }

    private fun ActivityMainBinding.setUpUi() {
        lifecycleOwner = this@MainActivity
        viewModel = mainViewModel

        authCard.apply {
            btnShowAccountProofData.setOnClickListener {
                val signatures = Fcl.currentUser?.accountProofSignatures
                    ?: throw Exception("Failed to get signatures")
                showDialog(title = "Account Proof Signatures", signatures.mapToString())
            }
            btnCopy.setOnClickListener {
                copyToClipboard("Address", requireAddress(), binding.coordinator)
            }
            btnOpenInFlowscan.setOnClickListener {
                openInExplorer("account/${requireAddress()}")
            }
        }

        with(txCard) {
            tvScript.text = SCRIPT
        }

        mainViewModel.address.observe(this@MainActivity) { address ->
            authCard.btnConnectWallet.setOnClickListener {
                if (address == null) {
                    it.showMenu(R.menu.menu_connect) { item ->
                        return@showMenu when (item.itemId) {
                            R.id.menu_item_authn_with_account_proof -> {
                                showConnectWalletDialog(Fcl.config.supportedWallets) { provider ->
                                    mainViewModel.connect(provider, true)
                                }
                                true
                            }
                            R.id.menu_item_authn_without_account_proof -> {
                                showConnectWalletDialog(Fcl.config.supportedWallets) { provider ->
                                    mainViewModel.connect(provider, false)
                                }
                                true
                            }
                            else -> false
                        }
                    }
                } else mainViewModel.disconnect()
            }

            mainViewModel.accountProofSignatures.observe(this@MainActivity) {
                authCard.btnShowAccountProofData.isVisible = it != null
            }

            mainViewModel.errorMessage.observe(this@MainActivity) {
                binding.coordinator.showSnackbar(it)
            }
        }
    }
}