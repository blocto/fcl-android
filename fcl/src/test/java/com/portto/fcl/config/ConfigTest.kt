package com.portto.fcl.config

import com.portto.fcl.FCL
import com.portto.fcl.config.Config.Companion.Key.*
import com.portto.fcl.config.Config.Companion.Network.TESTNET
import org.junit.Test

import org.junit.Assert.*

/**
 */
class ConfigTest {
    private var config: Config = FCL.config()

    @Test
    fun putValue_returnsValue() {
        config.put("foo", "bar")
        config.put(APP_TITLE, "Test Title")
        config.put(NETWORK, TESTNET.type)

        assertEquals("bar", config.get("foo"))
        assertEquals("Test Title", config.get(APP_TITLE))
        assertEquals("testnet", config.get(NETWORK))
    }

    @Test
    fun delete_returnsUpdated() {
        config.put("foo", "bar")
        assertEquals("bar", config.get("foo"))
        config.delete("foo")
        assertEquals(null, config.get("foo"))
    }
}