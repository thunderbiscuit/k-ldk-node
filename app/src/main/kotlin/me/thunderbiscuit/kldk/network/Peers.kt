package me.thunderbiscuit.kldk.network

import me.thunderbiscuit.kldk.channelManager
import me.thunderbiscuit.kldk.peerHandler
import me.thunderbiscuit.kldk.utils.toByteArray
import org.ldk.structs.Result__u832APIErrorZ
import org.ldk.structs.UserConfig
import java.net.InetSocketAddress

fun connectPeer(pubkey: String, hostname: String, port: Int): Unit {
    return try {
        peerHandler?.connect(
            pubkey.toByteArray(),
            InetSocketAddress(hostname, port),
            5000
        ) ?: throw(IllegalStateException("nioPeerHandler was not initialized"))
        println("Kldk successfully connected to peer $pubkey")
    } catch (e: Throwable) {
        println("Connect to peer exception: ${e.message}")
    }
}

fun createChannel(
    pubkey: ByteArray,
    channelValue: Long,
    pushAmount: Long = 0,
    userChannelId: Long,
    overrideConfig: UserConfig? = null
): Result__u832APIErrorZ? {
    val result: Result__u832APIErrorZ? = channelManager?.create_channel(pubkey, channelValue, pushAmount, userChannelId, overrideConfig)
    return result
}

// return Result__u832APIErrorZ, unwrap it and
// channelManager?.funding_transaction_generated(temporary_channel_id: tcid, funding_transaction: rawTxBytes)
