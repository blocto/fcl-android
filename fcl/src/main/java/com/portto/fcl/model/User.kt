package com.portto.fcl.model

import com.nftco.flow.sdk.FlowAddress


data class User(
    var fType: String? = "USER",
    var fVsn: String? = "1.0.0",
    val address: FlowAddress,
    var loggedIn: Boolean = false,
)