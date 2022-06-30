package com.portto.fcl.config

import com.portto.fcl.FCL
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
        config.put(Config.Key.APP_TITLE, "Test Title")
    }

    @Test
    fun get() {
        config.put(Config.Key.NETWORK, Config.Network.TESTNET.type)
        config.put("config.test.t", "t")

        assertEquals("Test Title", config.get(Config.Key.APP_TITLE))
        assertEquals("testnet", config.get(Config.Key.NETWORK))
        assertEquals("t", config.get("config.test.t"))
    }

    @Test
    fun getNotExisted() {
        assertEquals(null, config.get(Config.Key.APP_ICON))
    }

    @Test
    fun update() {
        config.update(Config.Key.APP_TITLE, "New Title")
        assertEquals("New Title", config.get(Config.Key.APP_TITLE))
    }

    @Test
    fun delete() {
        config.delete(Config.Key.APP_TITLE)
        assertEquals(null, config.get(Config.Key.APP_TITLE))
    }
}