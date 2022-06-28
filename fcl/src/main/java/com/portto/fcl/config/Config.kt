package com.portto.fcl.config

/**
 */
class Config() {

    private val configs: MutableMap<String, String> = mutableMapOf()

    fun put(key: Key, value: String): Config = apply { configs[key.value] = value }

    fun put(key: String, value: String): Config = apply { configs[key] = value }

    fun get(key: Key): String? = configs[key.value]

    fun get(key: String): String? = configs[key]

    fun delete(key: Key): Config = apply { configs.remove(key.value) }

    fun delete(key: String): Config = apply { configs.remove(key) }

    fun update(key: Key, newValue: String): Config = apply { configs[key.value] = newValue }

    fun update(key: String, newValue: String): Config = apply { configs[key] = newValue }

    fun clear(): Config = apply { configs.clear() }

    override fun toString(): String {
        return configs.toString()
    }

    companion object {
        /**
         * Common Configuration Keys
         */
        enum class Key(val value: String) {
            ACCESS_NODE_API("accessNode.api"),
            ACCESS_NODE_KEY("accessNode.key"),
            APP_TITLE("app.detail.title"),
            APP_ICON("app.detail.icon"),
//            WALLETS("discovery.wallet"),
            LIMIT("fcl.limit"),
            NETWORK("flow.network"),
            OPEN_ID_SCOPE("service.OpenID.scopes"),
        }


        enum class Network(val type: String) {
            LOCAL("local"),
            CANARYNET("canarynet"),
            TESTNET("testnet"),
            MAINNET("mainnet"),
        }
    }
}
