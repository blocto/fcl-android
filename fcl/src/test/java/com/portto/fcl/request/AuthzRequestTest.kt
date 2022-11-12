package com.portto.fcl.request

import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.cadence.DictionaryFieldEntry
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder
import com.nftco.flow.sdk.cadence.TYPE_ADDRESS
import com.nftco.flow.sdk.cadence.TYPE_ARRAY
import com.nftco.flow.sdk.cadence.TYPE_DICTIONARY
import com.nftco.flow.sdk.cadence.TYPE_INT
import com.nftco.flow.sdk.cadence.TYPE_STRING
import com.nftco.flow.sdk.cadence.TYPE_UFIX64
import com.nftco.flow.sdk.cadence.TYPE_UINT16
import com.portto.fcl.model.signable.toFclArgument
import com.portto.fcl.utils.NullableAnySerializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthzRequestTest {

    /**
     * Ref tx hash: e79bd94320c1da0d0c1a9ffdb27166072cb52a2cb2e630464c2e9b91276f716e
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun transform_flow_arg() = runTest {
        val args = with(JsonCadenceBuilder()) {
            listOf(
                FlowArgument(string("hello")),
                FlowArgument(int("-1")),
                FlowArgument(uint16("65535")),
                FlowArgument(ufix64("184467440737.12345")),
                FlowArgument(address("0x9e947494b44000d7")),
                FlowArgument(array { listOf(int("-101"), int("101")) }),
                FlowArgument(array { listOf(string("Clement"), string("Hannah")) }),
                FlowArgument(dictionary {
                    arrayOf(
                        DictionaryFieldEntry(Pair(boolean(true), int(1))),
                        DictionaryFieldEntry(Pair(boolean(false), int(0))),
                    )
                }),
            )
        }

        val builder = TxBuilder().apply {
            arguments(args)
        }

        val decodedArgs = builder.arguments.map { arg ->
            Json.decodeFromString(
                MapSerializer(String.serializer(), NullableAnySerializer),
                arg.stringValue
            )
        }

        val fclArgs = decodedArgs.map { it.toFclArgument() }
        Assert.assertEquals(8, fclArgs.size)

        with(fclArgs[0].arg) {
            Assert.assertEquals(TYPE_STRING, type)
            Assert.assertEquals("hello", value)
        }

        with(fclArgs[1].arg) {
            Assert.assertEquals(TYPE_INT, type)
            Assert.assertEquals("-1", value)
        }

        with(fclArgs[2].arg) {
            Assert.assertEquals(TYPE_UINT16, type)
            Assert.assertEquals("65535", value)
        }

        with(fclArgs[3].arg) {
            Assert.assertEquals(TYPE_UFIX64, type)
            Assert.assertEquals("184467440737.12345000", value)
        }

        with(fclArgs[4].arg) {
            Assert.assertEquals(TYPE_ADDRESS, type)
            Assert.assertEquals("0x9e947494b44000d7", value)
        }

        //  {
        //      "value":[
        //      {
        //          "value":"-101",
        //          "type":"Int"
        //      },
        //      {
        //          "value":"101",
        //          "type":"Int"
        //      }
        //      ],
        //      "type":"Array"
        //  }
        with(fclArgs[5].arg) {
            Assert.assertEquals(TYPE_ARRAY, type)
            Assert.assertEquals(
                listOf(
                    linkedMapOf("type" to "Int", "value" to "-101"),
                    linkedMapOf("type" to "Int", "value" to "101")
                ), value
            )
        }

        // {
        //     "value":[
        //     {
        //         "type":"String",
        //         "value":"Clement"
        //     },
        //     {
        //         "type":"String",
        //         "value":"Hannah"
        //     }
        //     ],
        //     "type":"Array"
        // }
        with(fclArgs[6].arg) {
            Assert.assertEquals(TYPE_ARRAY, type)
            Assert.assertEquals(
                listOf(
                    linkedMapOf("type" to "String", "value" to "Clement"),
                    linkedMapOf("type" to "String", "value" to "Hannah")
                ), value
            )
        }

        //  {
        //      "type":"Dictionary",
        //      "value":[
        //      {
        //          "key":{
        //              "type":"Bool",
        //              "value":true
        //          },
        //          "value":{
        //              "type":"Int",
        //              "value":"1"
        //          }
        //      },
        //      {
        //          "key":{
        //              "type":"Bool",
        //              "value":false
        //          },
        //          "value":{
        //              "type":"Int",
        //              "value":"0"
        //          }
        //      }
        //      ]
        //  }
        with(fclArgs[7].arg) {
            Assert.assertEquals(TYPE_DICTIONARY, type)
            println(value)
            Assert.assertEquals(
                listOf(
                    linkedMapOf(
                        "key" to linkedMapOf("type" to "Bool", "value" to true),
                        "value" to linkedMapOf("type" to "Int", "value" to "1")
                    ),
                    linkedMapOf(
                        "key" to linkedMapOf("type" to "Bool", "value" to false),
                        "value" to linkedMapOf("type" to "Int", "value" to "0")
                    ),
                ), value
            )
        }
    }
}
