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
import com.portto.fcl.sample.util.BLOCTO_MAINNET_APP_ID
import com.portto.fcl.sample.util.BLOCTO_TESTNET_APP_ID
import com.portto.fcl.sample.util.copyToClipboard
import com.portto.fcl.sample.util.hideKeyboard
import com.portto.fcl.sample.util.mapToString
import com.portto.fcl.sample.util.openInExplorer
import com.portto.fcl.sample.util.showDialog
import com.portto.fcl.sample.util.showMenu
import com.portto.fcl.sample.util.showSnackbar
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

        binding.setUpUi()

        mainViewModel.bindUi()
    }

    override fun onPause() {
        super.onPause()
        binding.root.clearFocus()
    }

    private fun ActivityMainBinding.setUpUi() {
        lifecycleOwner = this@MainActivity
        viewModel = mainViewModel

        binding.toggleButton.check(R.id.btn_testnet)
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) mainViewModel.setCurrentNetwork(checkedId == R.id.btn_mainnet)
        }

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
    }

    private fun MainViewModel.bindUi() {
        isCurrentMainnet.observe(this@MainActivity) {
            mainViewModel.disconnect()
            initFcl(it)
        }

        queryScript.observe(this@MainActivity) { script ->
            binding.queryCard.tvScript.text = script
            binding.queryCard.btnSendScript.setOnClickListener {
                binding.root.hideKeyboard()
                mainViewModel.sendQuery(script)
            }
        }

        mutateScript.observe(this@MainActivity) { script ->
            binding.mutateCard.tvScript.text = script
            binding.mutateCard.btnSendTx.setOnClickListener {
                binding.root.hideKeyboard()
                val userAddress = mainViewModel.address.value.orEmpty()
                mainViewModel.sendTransaction(script, userAddress)
            }
        }

        address.observe(this@MainActivity) { address ->
            binding.authCard.btnConnectWallet.setOnClickListener {
                if (address == null) it.showMenu(R.menu.menu_connect) { item ->
                    showConnectWalletDialog(this@MainActivity) {
                        mainViewModel.connect(
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
                message.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        it?.let {
                            binding.coordinator.showSnackbar(it)
                            mainViewModel.resetMessage()
                        }
                    }
            }

            transactionId.observe(this@MainActivity) {
                it?.let { txId ->
                    binding.mutateCard.btnCopy.setOnClickListener {
                        copyToClipboard("transaction ID", txId, binding.coordinator)
                    }
                    binding.mutateCard.btnOpenInFlowscan.setOnClickListener {
                        openInExplorer("transaction/$txId")
                    }
                }
            }
        }
    }

    private fun initFcl(isMainnet: Boolean) {
        Fcl.init(
            env = if (isMainnet) Network.MAINNET else Network.TESTNET,
            appDetail = AppDetail(),
            supportedWallets = getWalletList(isMainnet)
        )
        binding.coordinator.showSnackbar("Switched to ${if (isMainnet) "mainnet" else "testnet"}")
    }

    private fun getWalletList(isMainnet: Boolean) = listOf(
        Blocto.getInstance(if (isMainnet) BLOCTO_MAINNET_APP_ID else BLOCTO_TESTNET_APP_ID),
        Dapper
    )
}
