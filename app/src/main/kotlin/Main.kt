package me.thunderbiscuit.kldk

import org.ldk.batteries.ChannelManagerConstructor
import org.ldk.enums.ConfirmationTarget
import org.ldk.structs.*
import org.ldk.structs.FeeEstimator.FeeEstimatorInterface
import org.ldk.structs.Logger.LoggerInterface
import java.io.File

fun main() {
    println("Hello, ${Config.nodeName}!")

    // startNode()
}

// the startNode function fires up the basic things we need for the node to operate
fun startNode() {
    println("LDK starting...")

    val feeEstimator: FeeEstimator = FeeEstimator.new_impl(KldkFeeEstimator)

    val logger: Logger = Logger.new_impl(KldkLogger)

    val broadcaster: BroadcasterInterface = BroadcasterInterface.new_impl(KldkBroadcaster)

    val persister: Persist = Persist.new_impl(KldkPersister)

    val eventHandler: ChannelManagerConstructor.EventHandler = KldkEventHandler
}

// to create a FeeEstimator we need to provide an object that implement the FeeEstimatorInterface
// which has 1 function: get_est_sat_per_1000_weight(conf_target: ConfirmationTarget?): Int
object KldkFeeEstimator : FeeEstimatorInterface {
    override fun get_est_sat_per_1000_weight(confirmation_target: ConfirmationTarget?): Int {
        // we don't actually use the confirmation_target parameter and simply return 25_000 no matter what
        return 25_000
    }
}

// to create a Logger we need to provide an object that implements the LoggerInterface
// which has 1 function: log(record: Record?): Unit
object KldkLogger : LoggerInterface {
    override fun log(record: Record?) {
        println("$record")
    }
}

object KldkBroadcaster : BroadcasterInterface.BroadcasterInterfaceInterface {
    override fun broadcast_transaction(tx: ByteArray?): Unit {
        println(tx.toString())
    }
}

object KldkPersister : Persist.PersistInterface {
    override fun persist_new_channel(
        channel_id: OutPoint?,
        data: ChannelMonitor?,
        update_id: MonitorUpdateId?
    ): Result_NoneChannelMonitorUpdateErrZ? {
        if (channel_id == null || data == null) return null
        val channelMonitorBytes: ByteArray = data.write()
        File("${Config.homeDir}/channel_monitor_${byteArrayToHex(channel_id.to_channel_id())}.hex")
            .writeText(byteArrayToHex(channelMonitorBytes))
        return Result_NoneChannelMonitorUpdateErrZ.ok()
    }

    override fun update_persisted_channel(
        channel_id: OutPoint?,
        update: ChannelMonitorUpdate?,
        data: ChannelMonitor?,
        update_id: MonitorUpdateId?
    ): Result_NoneChannelMonitorUpdateErrZ? {
        if (channel_id == null || data == null) return null
        val channelMonitorBytes: ByteArray = data.write()
        File("${Config.homeDir}/channelmonitor${byteArrayToHex(channel_id.to_channel_id())}.hex")
            .writeText(byteArrayToHex(channelMonitorBytes))
        return Result_NoneChannelMonitorUpdateErrZ.ok()
    }
}

object KldkEventHandler : ChannelManagerConstructor.EventHandler {
    override fun handle_event(events: Event?) {
        when (events) {
            is Event.FundingGenerationReady -> println("We just had a FundingGenerationReady event")
            is Event.ChannelClosed          -> println("We just had a ChannelClosed event")
            is Event.DiscardFunding         -> println("We just had a DiscardFunding event")
            else                            -> println("We just had a $events event")
        }
    }

    override fun persist_manager(channel_manager_bytes: ByteArray?) {
        println("Persist manager")
        if (channel_manager_bytes != null) {
            val hex = byteArrayToHex(channel_manager_bytes)
            println("Channel manager bytes: $hex")
            File("${Config.homeDir}/channelmanager").writeText(byteArrayToHex(channel_manager_bytes))
        }
    }

    override fun persist_network_graph(network_graph: ByteArray?) {
        println("Implement network graph persistence")
    }
}