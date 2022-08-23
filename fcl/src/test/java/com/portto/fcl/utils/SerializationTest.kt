package com.portto.fcl.utils

import org.junit.Test

class SerializationTest {
    @Test
    fun test_deserialize() {
        val jsonString = """
      [
         {
            "f_type":"Service",
            "f_vsn":"1.0.0",
            "type":"open-id",
            "uid":"blocto#open-id",
            "method":"DATA",
            "data":{
               "f_type":"OpenID",
               "f_vsn":"1.0.0",
               "email":{
                  "email":"clement@portto.com",
                  "email_verified":true
               }
            }
         },
         {
            "f_type":"Service",
            "f_vsn":"1.0.0",
            "type":"account-proof",
            "method":"DATA",
            "uid":"blocto#account-proof",
            "data":{
               "f_type":"account-proof",
               "f_vsn":"2.0.0",
               "signatures":[
                  {
                     "f_type":"CompositeSignature",
                     "f_vsn":"1.0.0",
                     "addr":"0x28de65b3b5e83245",
                     "keyId":1,
                     "signature":"047d8b82f635cb54c23c1b399b0a5e7a811a034cc8ec05febf9e3fd01dc565d209725e052c09ae5a7b8fb843f6abef0893493494e46a6b7ad8915215c81b7bec"
                  }
               ],
               "address":"0x28de65b3b5e83245",
               "timestamp":null,
               "nonce":"75f8587e5bd5f9dcc9909d0dae1f0ac5814458b2ae129620502cb936fde7120a"
            }
         }
      ]
        """.trimIndent()



//        val deserializedFruitList: List<Service> =
//            json.decodeFromJsonElement(json.parseToJsonElement(jsonString))
//        println(deserializedFruitList)
    }
}