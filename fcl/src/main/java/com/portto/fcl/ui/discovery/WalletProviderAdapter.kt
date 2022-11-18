package com.portto.fcl.ui.discovery

import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.portto.fcl.Fcl
import com.portto.fcl.databinding.WalletDiscoveryDialogBinding
import com.portto.fcl.provider.Provider

fun Activity.showConnectWalletDialog(
    lifecycleOwner: LifecycleOwner,
    onWalletSelected: (Provider) -> Unit
) {
    val dialog = MaterialAlertDialogBuilder(this).create()

    class DialogObserver : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            dialog.dismiss()
        }
    }
    lifecycleOwner.lifecycle.addObserver(DialogObserver())
    val providerAdapter = WalletProviderAdapter(dialog, onWalletSelected)
    val binding: WalletDiscoveryDialogBinding =
        WalletDiscoveryDialogBinding.inflate(layoutInflater).apply {
            providerList.adapter = providerAdapter
            providerList.setHasFixedSize(true)
        }
    dialog.setView(binding.root)
    providerAdapter.submitList(Fcl.config.supportedWallets)
    dialog.show()
}

internal class WalletProviderAdapter(
    private val dialog: Dialog,
    private val onWalletProviderClick: (Provider) -> Unit
) :
    ListAdapter<Provider, ProviderViewHolder>(ProviderDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder =
        ProviderViewHolder.from(parent)

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, dialog, onWalletProviderClick)
        }
    }
}

internal object ProviderDiffCallback : DiffUtil.ItemCallback<Provider>() {
    override fun areItemsTheSame(oldItem: Provider, newItem: Provider) =
        oldItem.info.title == newItem.info.title

    override fun areContentsTheSame(oldItem: Provider, newItem: Provider) =
        oldItem.info.title == newItem.info.title
}
