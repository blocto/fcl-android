package com.portto.fcl.ui.discovery

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.portto.fcl.databinding.ListItemWalletProviderBinding
import com.portto.fcl.provider.Provider

internal class ProviderViewHolder(private val binding: ListItemWalletProviderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(provider: Provider, dialog: Dialog, onClick: (provider: Provider) -> Unit) {
        binding.apply {
            tvTitle.text = provider.info.title
            tvDescription.text = provider.info.description
            imgIcon.load(provider.info.icon)
            root.setOnClickListener {
                onClick(provider)
                dialog.dismiss()
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): ProviderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemWalletProviderBinding.inflate(layoutInflater, parent, false)
            return ProviderViewHolder(binding)
        }
    }
}
