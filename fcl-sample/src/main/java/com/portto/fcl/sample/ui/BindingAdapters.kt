package com.portto.fcl.sample.ui

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.sample.util.mapToString

@BindingAdapter("composite_signatures")
fun TextView.bindCompositeSignatures(compositeSignatures: List<CompositeSignature>?) =
    if (compositeSignatures == null) {
        text = ""
        isVisible = false
    } else {
        text = compositeSignatures.mapToString()
        isVisible = true
    }
