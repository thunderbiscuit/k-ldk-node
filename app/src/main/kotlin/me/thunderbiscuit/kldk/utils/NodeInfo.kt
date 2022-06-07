package me.thunderbiscuit.kldk.utils

import me.thunderbiscuit.kldk.Node

fun listPeers(node: Node): List<String> {
    val peersByteArray = node.peerManager.get_peer_node_ids()
    val peersList = peersByteArray.map {
        it.toHex()
    }
    return peersList
}

fun getNodeId(node: Node): String {
    return node.channelManager.get_our_node_id().toHex()
}
