package me.thunderbiscuit.kldk

import mu.KotlinLogging
import org.ldk.batteries.NioPeerHandler
import org.ldk.structs.ChannelManager
import org.ldk.structs.PeerManager

// the Node singleton is really just a way to namespace and keep a reference to
// the peerHandler, peerManager, and channelManager so we can use them throughout the app
// and they don't get garbage collected
object Node {

    private val MuLogger = KotlinLogging.logger("BaseLogger")

    val peerHandler: NioPeerHandler
    val peerManager: PeerManager
    val channelManager: ChannelManager

    init {
        val coreNodeElements: CoreNodeElements = startNode()
        // peerHandler, peerManager, channelManager = startNode() // doesn't work
        peerHandler = coreNodeElements.peerHandler
        peerManager = coreNodeElements.peerManager
        channelManager = coreNodeElements.channelManager
    }

    fun initialize() {
        MuLogger.info { "Node object initialized" }
    }
}
