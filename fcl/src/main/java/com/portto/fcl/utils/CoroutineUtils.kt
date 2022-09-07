package com.portto.fcl.utils

import kotlinx.coroutines.delay

internal object CoroutineUtils {
    suspend fun repeatWhen(predicate: suspend () -> Boolean, block: suspend () -> Unit) {
        while (predicate()) {
            block.invoke()
        }
    }

    suspend fun runBlockDelay(timeMillis: Long, block: suspend () -> Unit) {
        val startTime = System.currentTimeMillis()
        block.invoke()
        val elapsedTime = System.currentTimeMillis() - startTime
        delay(timeMillis - elapsedTime)
    }
}
