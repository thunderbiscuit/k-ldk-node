package me.thunderbiscuit.kldk.utils

import me.thunderbiscuit.kldk.Node

fun listPeers(): List<String> {
    val peersByteArray = Node.peerManager?.get_peer_node_ids() ?: throw (IllegalStateException("peerManager was not initialized"))
    val peersList = peersByteArray.map {
        it.toHex()
    }
    return peersList
}

fun getNodeId(): String {
    return Node.channelManager?.get_our_node_id()?.toHex() ?: ""
}
