package com.portto.fcl.sample.repository

import com.portto.fcl.FCL
import com.portto.fcl.model.discovery.Service


class FclRepository {
    suspend fun getSupportedWallets(): List<Service> = FCL.discoverWallets()
}