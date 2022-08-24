package com.portto.fcl.resolve

import com.portto.fcl.model.signable.Interaction

interface Resolver {
    suspend fun resolve(ix: Interaction)
}