package me.thunderbiscuit.kldk.network

import me.thunderbiscuit.kldk.nioPeerHandler
import java.net.InetSocketAddress

fun connectPeer(pubkey: String, hostname: String, port: Int): Unit {
    return try {
        nioPeerHandler?.connect(
            pubkey.toByteArray(),
            InetSocketAddress(hostname, port),
            5000
        ) ?: throw(IllegalStateException("nioPeerHandler was not initialized"))
        println("Kldk successfully connected to peer $pubkey")
    } catch (e: Throwable) {
        println("Connect to peer exception: ${e.message}")
    }
}
