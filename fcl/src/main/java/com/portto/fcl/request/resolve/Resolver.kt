package com.portto.fcl.request.resolve

import com.portto.fcl.model.signable.Interaction

internal interface Resolver {
    suspend fun resolve(ix: Interaction)
}
