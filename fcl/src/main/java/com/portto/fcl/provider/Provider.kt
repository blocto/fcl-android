package com.portto.fcl.provider

import java.net.URL

interface Provider {
    val title: String
    val endpoint: URL
}