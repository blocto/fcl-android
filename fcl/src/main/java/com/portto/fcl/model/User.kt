package com.portto.fcl.model


data class User(
    var fType: String? = "USER",
    var fVsn: String? = "1.0.0",
    val address: String,
    var loggedIn: Boolean = false,
)