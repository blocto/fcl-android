package com.portto.fcl.model.signable

import kotlinx.serialization.Serializable

@Serializable
data class Roles(
    var authorizer: Boolean = false,
    var payer: Boolean = false,
    var proposer: Boolean = false
) {
    fun merge(role: Roles) {
        proposer = proposer || role.proposer
        authorizer = authorizer || role.authorizer
        payer = payer || role.payer
    }
}