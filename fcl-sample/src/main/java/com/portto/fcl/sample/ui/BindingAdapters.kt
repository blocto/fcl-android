package com.portto.fcl.sample.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.portto.fcl.model.discovery.Service
import com.portto.fcl.sample.util.isDark
import com.portto.fcl.sample.util.showDialog

@BindingAdapter("walletProviders")
fun ChipGroup.bindWalletProviders(services: List<Service>?) {
    removeAllViews()
    services?.forEach { service ->
        val chip = Chip(context).apply {
            val bgColor = Color.parseColor(service.provider?.color)
            text = service.provider?.name
            setTextColor(if (bgColor.isDark()) Color.WHITE else Color.BLACK)
            chipStrokeWidth = 0f
            chipBackgroundColor = ColorStateList.valueOf(bgColor)
            setOnClickListener {
                context.showDialog(service.provider?.name, service.provider.toString())
            }
        }
        addView(chip)
    }
}