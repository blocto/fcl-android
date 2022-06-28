package com.portto.fcl.config

import com.portto.fcl.FCL
import com.portto.fcl.config.Config.Companion.Key.*
import com.portto.fcl.config.Config.Companion.Network.TESTNET
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 */
class ConfigTest {
    private var config: Config = FCL.config()

    @Before
    fun setUp(){
        config.clear()
        config.put(APP_TITLE, "Test Title")
    }

    @Test
    fun get() {
        config.put(NETWORK, TESTNET.type)
        config.put("config.test.t", "t")

        assertEquals("Test Title", config.get(APP_TITLE))
        assertEquals("testnet", config.get(NETWORK))
        assertEquals("t", config.get("config.test.t"))
    }

    @Test
    fun getNotExisted() {
        assertEquals(null, config.get(APP_ICON))
    }

    @Test
    fun update() {
        config.update(APP_TITLE, "New Title")
        assertEquals("New Title", config.get(APP_TITLE))
    }

    @Test
    fun delete() {
        config.delete(APP_TITLE)
        assertEquals(null, config.get(APP_TITLE))
    }
}