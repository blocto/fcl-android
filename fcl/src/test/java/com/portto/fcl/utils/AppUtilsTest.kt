package com.portto.fcl.utils

import com.nftco.flow.sdk.cadence.AddressField
import com.nftco.flow.sdk.cadence.IntNumberField
import com.portto.fcl.Fcl
import com.portto.fcl.config.AppDetail
import com.portto.fcl.config.Network
import com.portto.fcl.provider.dapper.Dapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppUtilsTest {

    @Before
    fun setUp() {
        Fcl.init(
            appDetail = AppDetail("test-title"),
            env = Network.TESTNET,
            supportedWallets = listOf(Dapper)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun query_with_arguments() = runTest {
        val script = """
            pub fun main(a: Int, b: Int, addr: Address): Int {
              log(addr)
            return a + b
         }
        """.trimIndent()

        try {
            val result = AppUtils.query(
                queryScript = script,
                args = listOf(
                    IntNumberField("2"),
                    IntNumberField("3"),
                    AddressField("0xba1132bc08f82fe2")
                )
            )
            Assert.assertEquals("5", result.value)
        } catch (e: Exception) {
            throw e
        }
    }
}