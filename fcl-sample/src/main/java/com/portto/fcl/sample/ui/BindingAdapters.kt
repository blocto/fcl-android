package com.portto.fcl.sample.ui

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.portto.fcl.model.discovery.Service

@BindingAdapter("walletProviders")
fun ChipGroup.bindWalletProviders(services: List<Service>?) {
    services?.forEach {
        val chip = Chip(context).apply {
            val bgColor = Color.parseColor(it.provider?.color)
            text = it.provider?.name
            setTextColor(
                if (isDarkColor(bgColor)) Color.WHITE
                else Color.BLACK
            )
            chipStrokeWidth = 0f
            chipBackgroundColor = ColorStateList.valueOf(bgColor)
        }
        addView(chip)
    }
}

fun isDarkColor(@ColorInt color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) < 0.5
}