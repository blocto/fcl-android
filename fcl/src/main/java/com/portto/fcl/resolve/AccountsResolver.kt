package com.portto.fcl.resolve

import com.portto.fcl.Fcl
import com.portto.fcl.model.AuthData
import com.portto.fcl.model.PollingResponse
import com.portto.fcl.model.service.Service
import com.portto.fcl.model.service.ServiceType
import com.portto.fcl.model.signable.*
import com.portto.fcl.network.execHttpPost
import com.portto.fcl.utils.FclError
import com.portto.fcl.utils.toDataClass
import com.portto.fcl.utils.toJsonObject

class AccountsResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        if (!ix.isTransaction()) {
            return
        }

        collectAccounts(ix)
    }

    private suspend fun collectAccounts(ix: Interaction) {
        val currentUser = Fcl.currentUser
            ?: throw FclError.UnauthenticatedException()

        val service = currentUser.services?.find { it.type == ServiceType.PRE_AUTHZ }
            ?: throw FclError.ServiceNotFoundException()

        val endpoint = service.endpoint
            ?: throw FclError.GeneralException("Endpoint not found.")

        val preSignable = ix.buildPreSignable(Roles())

        val response = execHttpPost(endpoint, service.params, data = preSignable.toJsonObject())

        val signableUsers = response.getAccounts()

        val accounts = mutableMapOf<String, SignableUser>()

        ix.authorizations.clear()

        signableUsers.forEach { user ->
            val tempID = "${user.address}-${user.keyId}"
            user.tempId = tempID

            if (accounts.keys.contains(tempID)) {
                accounts[tempID]?.role?.merge(user.role)
            }

            accounts[tempID] = user

            if (user.role.proposer) {
                ix.proposer = tempID
            }

            if (user.role.payer) {
                ix.payer = tempID
            }

            if (user.role.authorizer) {
                ix.authorizations.add(tempID)
            }
        }

        ix.accounts = accounts
    }

    private fun PollingResponse.getAccounts(): List<SignableUser> {
        val axs = mutableListOf<Pair<String, Service>>()

        val authData = data?.toDataClass<AuthData>()

        authData?.proposer?.let { axs.add(Pair("PROPOSER", it)) }

        authData?.payer?.forEach { axs.add(Pair("PAYER", it)) }

        authData?.authorization?.forEach { axs.add(Pair("AUTHORIZER", it)) }

        return axs.mapNotNull {
            val role = it.first
            val service = it.second
            val address = service.identity?.address
            val keyId = service.identity?.keyId

            if (address == null || keyId == null) {
                null
            } else {
                SignableUser(
                    tempId = "$address|$keyId",
                    address = address,
                    keyId = keyId,
                    role = Roles(
                        proposer = role == "PROPOSER",
                        authorizer = role == "AUTHORIZER",
                        payer = role == "PAYER",
                    )
                ) { data ->
                    val endpoint = service.endpoint
                        ?: throw FclError.GeneralException("Endpoint is null.")
                    val params = service.params
                        ?: throw FclError.GeneralException("Params is null.")
                    execHttpPost(endpoint, params = params, data = data.toJsonObject())
                }
            }
        }
    }
}