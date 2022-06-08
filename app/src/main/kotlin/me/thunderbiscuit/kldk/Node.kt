package me.thunderbiscuit.kldk

import mu.KotlinLogging
import org.ldk.batteries.NioPeerHandler
import org.ldk.structs.ChannelManager
import org.ldk.structs.PeerManager
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

// the Node singleton is really just a way to namespace and keep references to
// the peerHandler, peerManager, and channelManager so we can use them throughout the app
// and they don't get garbage collected
object Node {

    // private val MuLogger = KotlinLogging.logger("BaseLogger")
    private val timer = Timer(true)

    val peerHandler: NioPeerHandler
    val peerManager: PeerManager
    val channelManager: ChannelManager

    init {
        val coreNodeElements: CoreNodeElements = startNode()
        peerHandler = coreNodeElements.peerHandler
        peerManager = coreNodeElements.peerManager
        channelManager = coreNodeElements.channelManager
    }

    fun initialize() {
        // MuLogger.info { "Node object initialized" }
        println("Node object initialized...")
        timer.schedule(
            delay = 0.seconds.inWholeMilliseconds,
            period = 10.seconds.inWholeMilliseconds
        ) {
            println("Object is still alive")
        }
    }
}
