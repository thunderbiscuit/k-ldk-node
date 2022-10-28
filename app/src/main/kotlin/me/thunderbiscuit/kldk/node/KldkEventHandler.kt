package me.thunderbiscuit.kldk.node

import me.thunderbiscuit.kldk.Node
import me.thunderbiscuit.kldk.network.broadcastFundingTx
import me.thunderbiscuit.kldk.network.buildOnChainTx
import me.thunderbiscuit.kldk.utils.Config
import me.thunderbiscuit.kldk.utils.toHex
import mu.KotlinLogging
import org.bitcoindevkit.Network
import org.ldk.batteries.ChannelManagerConstructor
import org.ldk.structs.Event
import java.io.File

private val MuLogger = KotlinLogging.logger("BaseLogger")

object KldkEventHandler : ChannelManagerConstructor.EventHandler {
    override fun handle_event(event: Event?) {
        println("Root of event handler: ${event.toString()}")
        when (event) {
            is Event.FundingGenerationReady -> fundingGenerationReady(event)
            is Event.ChannelClosed          -> channelClosedEvent(event)
            is Event.DiscardFunding         -> println("We just had a DiscardFunding event")
            else                            -> println("We just had a $event event")
        }
    }

    override fun persist_manager(channel_manager_bytes: ByteArray?) {
        if (channel_manager_bytes != null) {
            File("${Config.homeDir}/channelmanager").writeText(channel_manager_bytes.toHex())
        }
    }

    override fun persist_network_graph(network_graph: ByteArray?) {
        println("Implement network graph persistence")
    }

    override fun persist_scorer(scorer_bytes: ByteArray?) {
        println("Implement scorer persistence")
    }
}

fun channelClosedEvent(event: Event.ChannelClosed) {
    println("We just had a ChannelClosed event")
    println("The event reason is: ${event.reason}")
    println("The event channel ID is: ${event.channel_id}")

    MuLogger.info { "We just had a ChannelClosed event" }
    MuLogger.info { "The event reason is: ${event.reason}" }
    MuLogger.info { "The event channel ID is: ${event.channel_id}" }
}

fun fundingGenerationReady(event: Event.FundingGenerationReady) {
    println("We just had a FundingGenerationReady event")
    println("The output script for the funding transaction is: ${event.output_script.toHex()}")
    println("The channel value in satoshis for the funding transaction is: ${event.channel_value_satoshis}")
    println("The temporary channel ID for the funding transaction is: ${event.temporary_channel_id.toHex()}")
    val fundingTx: ByteArray = buildOnChainTx(
        network = Network.TESTNET,
        value = event.channel_value_satoshis,
        script = event.output_script
    )
    println("The funding tx is ${fundingTx.toHex()}")
    // broadcastFundingTx(Node, event.temporary_channel_id, event.counterparty_node_id, fundingTx)

    MuLogger.info { "We just had a FundingGenerationReady event" }
    MuLogger.info { "The output script for the funding transaction is: ${event.output_script.toHex()}" }
    MuLogger.info { "The channel value in satoshis for the funding transaction is: ${event.channel_value_satoshis}" }
    MuLogger.info { "The temporary channel ID for the funding transaction is: ${event.temporary_channel_id.toHex()}" }
}
