package me.thunderbiscuit.kldk.node

import org.ldk.structs.BroadcasterInterface

object KldkBroadcaster : BroadcasterInterface.BroadcasterInterfaceInterface {
    override fun broadcast_transaction(tx: ByteArray?): Unit {
        println(tx.toString())
    }
}
