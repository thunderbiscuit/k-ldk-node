package me.thunderbiscuit.kldk.network

import me.thunderbiscuit.kldk.Node
import me.thunderbiscuit.kldk.utils.toByteArray
import org.ldk.structs.Result_NoneAPIErrorZ
import org.ldk.structs.Result__u832APIErrorZ
import org.ldk.structs.UserConfig
import java.net.InetSocketAddress

fun connectPeer(Node: Node, pubkey: String, hostname: String, port: Int): String {
    return try {
        Node.peerHandler.connect(
            pubkey.toByteArray(),
            InetSocketAddress(hostname, port),
            5000
        )
        "No error thrown when attempting to connect to peer $pubkey"
    } catch (e: Throwable) {
        "Connect to peer exception: ${e.message}"
    }
}

fun createFundingTx(
    Node: Node,
    pubkey: ByteArray,
    channelValue: Long,
    pushAmount: Long = 0,
    userChannelId: Long,
    overrideConfig: UserConfig? = null
): Result__u832APIErrorZ? {
    val result: Result__u832APIErrorZ? = Node.channelManager.create_channel(pubkey, channelValue, pushAmount, userChannelId, overrideConfig)
    return result
}

fun broadcastFundingTx(
    Node: Node,
    tempChannelId: String,
    counterPartyNodeId: String,
    fundingTx: String,
): Result_NoneAPIErrorZ? {
    return Node.channelManager.funding_transaction_generated(tempChannelId.toByteArray(), counterPartyNodeId.toByteArray(), fundingTx.toByteArray())
}

// fun listChannels(): Array<out ChannelDetails>? {
//     return channelManager?.list_channels()
// }