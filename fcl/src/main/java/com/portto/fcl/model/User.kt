package com.portto.fcl.model

import com.portto.fcl.model.authn.AccountProofData

data class User(
    var fType: String? = "USER",
    var fVsn: String? = "1.0.0",
    // user authenticated address
    val address: String,
    // user signed account proof
    val accountProofData: AccountProofData? = null,
)