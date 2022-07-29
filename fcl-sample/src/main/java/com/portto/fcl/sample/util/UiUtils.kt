package com.portto.fcl.sample.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.portto.fcl.Fcl
import com.portto.fcl.sample.R

/**
 * Determine the provided color is whether dark or not
 */
fun Int.isDark(): Boolean = ColorUtils.calculateLuminance(this) < 0.5

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.showDialog(title: String? = null, msg: String) {
    MaterialAlertDialogBuilder(this).apply {
        setTitle(title)
        setMessage(msg)
        setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
    }.show()
}

fun View.showMenu(
    @MenuRes menuRes: Int,
    menuItemClickListener: PopupMenu.OnMenuItemClickListener
) {
    val popup = PopupMenu(context, this)
    popup.menuInflater.inflate(menuRes, popup.menu)
    popup.setOnMenuItemClickListener(menuItemClickListener)
    popup.show()
}

/**
 * Open the provided [path] in Flowscan
 */
fun Context.openInExplorer(path: String) {
    val uri = Uri.Builder()
        .scheme("https")
        .authority(if (Fcl.isMainnet) "flowscan.org" else "testnet.flowscan.org")
        .path(path)
        .build()
    startActivity(Intent(Intent.ACTION_VIEW, uri))
}

/**
 * Copy the [message] to clipboard
 */
fun Context.copyToClipboard(label: String, message: String, snackbarRootView: View) {
    val clipboard = ContextCompat.getSystemService(
        this, ClipboardManager::class.java
    )
    val clip = ClipData.newPlainText(label, message)
    clipboard?.setPrimaryClip(clip)
    snackbarRootView.showSnackbar("$label has been copied to clipboard!")
}