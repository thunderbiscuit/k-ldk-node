package me.thunderbiscuit.kldk.node

import kotlinx.coroutines.*
import me.thunderbiscuit.kldk.network.blockstreamBroadcastTx
import mu.KLogger
import mu.KotlinLogging
import org.ldk.structs.BroadcasterInterface

object KldkBroadcaster : BroadcasterInterface.BroadcasterInterfaceInterface {
    override fun broadcast_transaction(tx: ByteArray?): Unit {
        tx?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val txid: String = blockstreamBroadcastTx(tx)
                val logger: KLogger = KotlinLogging.logger("BaseLogger")
                logger.info { "We've broadcast a transaction with txid $txid" }
            }
        } ?: throw(IllegalStateException("Broadcaster attempted to broadcast an null transaction"))
    }
}
