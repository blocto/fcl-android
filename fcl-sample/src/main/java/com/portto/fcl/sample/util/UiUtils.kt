package com.portto.fcl.sample.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.portto.fcl.Fcl

/**
 * Determine the provided color is whether dark or not
 */
fun Int.isDark(): Boolean = ColorUtils.calculateLuminance(this) < 0.5

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Show a alert dialog
 *
 * @param title Optional; Title to be shown
 * @param message Body of the alert dialog
 * @param action pair of action title and [DialogInterface.OnClickListener]
 */
fun Context.showDialog(
    title: String? = null,
    message: String,
    action: Pair<String, DialogInterface.OnClickListener>
) {
    MaterialAlertDialogBuilder(this).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(action.first, action.second)
        setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
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
