package me.thunderbiscuit.kldk.utils

import me.thunderbiscuit.kldk.peerManager

fun listPeers(): List<String> {
    val peersByteArray = peerManager?.get_peer_node_ids() ?: throw (IllegalStateException("peerManager was not initialized"))
    val peersList = peersByteArray.map {
        it.toHex()
    }
    return peersList
}
