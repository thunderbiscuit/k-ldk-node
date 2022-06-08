package me.thunderbiscuit.kldk

import mu.KotlinLogging
import org.ldk.batteries.ChannelManagerConstructor
import org.ldk.batteries.NioPeerHandler
import org.ldk.structs.ChannelManager
import org.ldk.structs.PeerManager

// the Node singleton is really just a way to namespace and keep references to
// the peerHandler, peerManager, channelManager, and channelManagerConstructor
// so we can use them throughout the app and they don't get garbage collected
object Node {

    // private val MuLogger = KotlinLogging.logger("BaseLogger")

    val peerHandler: NioPeerHandler
    val peerManager: PeerManager
    val channelManager: ChannelManager
    val channelManagerConstructor: ChannelManagerConstructor

    init {
        val coreNodeElements: CoreNodeElements = startNode()
        peerHandler = coreNodeElements.peerHandler
        peerManager = coreNodeElements.peerManager
        channelManager = coreNodeElements.channelManager
        channelManagerConstructor = coreNodeElements.channelManagerConstructor

        // MuLogger.info {
        //     "Node object initialized"
        // }
    }

    fun initialize() {
        return Unit
    }
}
