package com.portto.fcl.utils

import com.nftco.flow.sdk.FlowException
import com.nftco.flow.sdk.cadence.AddressField
import com.nftco.flow.sdk.cadence.IntNumberField
import com.portto.fcl.Fcl
import com.portto.fcl.config.Network
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.provider.dapper.Dapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppUtilsTest {

    private val sig1 = CompositeSignature("0x123", "0", "abc123")
    private val sig2 = CompositeSignature("0x123", "0", "abc123")
    private val sig3 = CompositeSignature("0x456", "0", "abc123")

    @Before
    fun setUp() {
        Fcl.init(env = Network.TESTNET, supportedWallets = listOf(Dapper))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun verifyUserSignatures_missingSignatures_throwsException() = runTest {
        try {
            AppUtils.verifyUserSignatures("FOO", emptyList())
            Assert.fail("Signatures can't be empty")
        } catch (e: Exception) {
            Assert.assertEquals(true, e is NoSuchElementException)
            Assert.assertEquals("List is empty.", e.message)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun verifyUserSignatures_differentAccountAddresses_throwsException() = runTest {
        try {
            AppUtils.verifyUserSignatures("FOO", listOf(sig1, sig2, sig3))
            Assert.fail("Account addresses are different")
        } catch (e: Exception) {
            Assert.assertEquals(true, e is FlowException)
            Assert.assertEquals("Error while running script", e.message)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun query_scriptsAndArgs_returnsResult() = runTest {
        val script = """
            pub fun main(a: Int, b: Int, addr: Address): Int {
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
