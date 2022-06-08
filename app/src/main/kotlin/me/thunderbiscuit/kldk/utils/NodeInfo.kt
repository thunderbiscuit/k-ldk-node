package me.thunderbiscuit.kldk.utils

import me.thunderbiscuit.kldk.Node

fun listPeers(Node: Node): List<String> {
    val peersByteArray = Node.peerManager.get_peer_node_ids()
    val peersList = peersByteArray.map {
        it.toHex()
    }
    return peersList
}

fun getNodeId(Node: Node): String {
    return Node.channelManager.get_our_node_id().toHex()
}
