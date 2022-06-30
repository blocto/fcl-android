package com.portto.fcl.sample.util

import android.content.Context
import androidx.core.graphics.ColorUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Determine the provided color is whether dark or not
 */
fun Int.isDark(): Boolean = ColorUtils.calculateLuminance(this) < 0.5


fun Context.showDialog(title: String? = null, msg: String) {
    MaterialAlertDialogBuilder(this).apply {
        setTitle(title)
        setMessage(msg)
        setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
    }.show()
}