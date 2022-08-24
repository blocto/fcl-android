package com.portto.fcl.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object CoroutineUtils {
    fun ioScope(unit: suspend () -> Unit) = CoroutineScope(Dispatchers.IO).launch { unit.invoke() }

    suspend fun repeatWhen(
        predicate: suspend () -> Boolean,
        block: suspend () -> Unit,
    ) {
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