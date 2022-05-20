package me.thunderbiscuit.kldk.node

import me.thunderbiscuit.kldk.channelManager
import me.thunderbiscuit.kldk.utils.Config
import me.thunderbiscuit.kldk.utils.toHex
import org.ldk.batteries.ChannelManagerConstructor
import org.ldk.structs.Event
import java.io.File

object KldkEventHandler : ChannelManagerConstructor.EventHandler {
    override fun handle_event(event: Event?) {
        when (event) {
            is Event.FundingGenerationReady -> fundingGenerationReady(event)
            is Event.ChannelClosed          -> println("We just had a ChannelClosed event")
            is Event.DiscardFunding         -> println("We just had a DiscardFunding event")
            else                            -> println("We just had a $event event")
        }
    }

    override fun persist_manager(channel_manager_bytes: ByteArray?) {
        println("Persist manager")
        if (channel_manager_bytes != null) {
            val hex = channel_manager_bytes.toHex()
            println("Channel manager bytes: $hex")
            File("${Config.homeDir}/channelmanager").writeText(channel_manager_bytes.toHex())
        }
    }

    override fun persist_network_graph(network_graph: ByteArray?) {
        println("Implement network graph persistence")
    }
}

fun fundingGenerationReady(event: Event.FundingGenerationReady) {
    println("We just had a FundingGenerationReady event")
    println(event.output_script)
    println(event.channel_value_satoshis)
    // transform outputscript into a signed raw transaction
    // channelManager?.funding_transaction_generated(temporary_channel_id: ByteArray, funding_transaction: ByteArray)
}
