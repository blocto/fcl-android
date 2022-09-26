package com.portto.fcl.config

import com.portto.fcl.Fcl
import com.portto.fcl.model.Replacement
import com.portto.fcl.provider.dapper.Dapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 */
class ConfigTest {
    @Before
    fun setUp() {
        Fcl.init(
            appDetail = AppDetail("test-title"),
            env = Network.TESTNET,
            supportedWallets = listOf(Dapper)
        )

        Fcl.config.put(
            Config.Option.AddressReplacement(
                listOf(Replacement("abc", "0x123"), Replacement("def", "0x456"))
            )
        )
    }

    @Test
    fun get_config() {
        assertEquals("test-title", Fcl.config.appDetail?.title)
        assertEquals(Network.TESTNET, Fcl.config.env)
        assertEquals(false, Fcl.isMainnet)
        assertEquals("def", Fcl.config.addressReplacement.last().placeholder)
    }
}
