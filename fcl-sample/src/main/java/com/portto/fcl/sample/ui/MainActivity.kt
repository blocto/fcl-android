package com.portto.fcl.sample.ui

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.portto.fcl.Fcl
import com.portto.fcl.config.AppDetail
import com.portto.fcl.config.Network
import com.portto.fcl.provider.blocto.Blocto
import com.portto.fcl.provider.dapper.Dapper
import com.portto.fcl.sample.R
import com.portto.fcl.sample.databinding.ActivityMainBinding
import com.portto.fcl.sample.util.*
import com.portto.fcl.ui.discovery.showConnectWalletDialog
import kotlinx.coroutines.launch
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
            env = Network.TESTNET,
            appDetail = AppDetail(),
            supportedWallets = listOf(
                Blocto.getInstance(bloctoAppId = BLOCTO_APP_ID, isDebug = true),
                Dapper,
            )
        )

        binding.setUpUi()

        mainViewModel.bindUi()
    }

    private fun ActivityMainBinding.setUpUi() {
        lifecycleOwner = this@MainActivity
        viewModel = mainViewModel

        authCard.apply {
            btnShowAccountProofData.setOnClickListener {
                val accountProofData = Fcl.currentUser?.accountProofData
                    ?: throw Exception("Failed to get account proof data")

                showDialog(title = "Account Proof Signatures",
                    message = accountProofData.signatures.mapToString(),
                    action = "Verify" to DialogInterface.OnClickListener { dialog, _ ->
                        mainViewModel.verifySignature(true)
                        dialog.dismiss()
                    }
                )
            }
            btnCopy.setOnClickListener {
                copyToClipboard("Address", requireAddress(), binding.coordinator)
            }
            btnOpenInFlowscan.setOnClickListener {
                openInExplorer("account/${requireAddress()}")
            }
        }

        txCard.apply {
            tvScript.text = SCRIPT
        }
    }

    private fun MainViewModel.bindUi() {
        address.observe(this@MainActivity) { address ->
            binding.authCard.btnConnectWallet.setOnClickListener {
                if (address == null) it.showMenu(R.menu.menu_connect) { item ->
                    showConnectWalletDialog(Fcl.config.supportedWallets) { provider ->
                        mainViewModel.connect(
                            walletProvider = provider,
                            withAccountPoof = item.itemId == R.id.menu_item_authn_with_account_proof
                        )
                    }
                    return@showMenu true
                }
                else mainViewModel.disconnect()
            }

            accountProofSignatures.observe(this@MainActivity) {
                binding.authCard.btnShowAccountProofData.isVisible = it != null
            }

            lifecycleScope.launch {
                mainViewModel.message.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        it?.let {
                            binding.coordinator.showSnackbar(it)
                            mainViewModel.resetMessage()
                        }
                    }
            }
        }
    }
}