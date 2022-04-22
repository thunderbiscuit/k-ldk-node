package me.thunderbiscuit.kldk

import org.ldk.enums.ConfirmationTarget
import org.ldk.structs.*
import org.ldk.structs.FeeEstimator.FeeEstimatorInterface
import org.ldk.structs.Logger.LoggerInterface
import java.io.File

fun main() {
    println("Hello, lightning!")
    println("Hello, ${Config.me}!")

    // startNode()
}

// the startNode function fires up the basic things we need for the node to operate
fun startNode() {
    println("LDK starting...")

    val feeEstimator: FeeEstimator = FeeEstimator.new_impl(KldkFeeEstimator)

    val logger: Logger = Logger.new_impl(KldkLogger)

    val broadcasterInterface: BroadcasterInterface = BroadcasterInterface.new_impl(KldkBroadcaster)

    val persister: Persist = Persist.new_impl(KldkPersister)
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
        File("${Config.homeDir}/channel_monitor_${byteArrayToHex(channel_id.to_channel_id())}.hex")
            .writeText(byteArrayToHex(channelMonitorBytes))
        return Result_NoneChannelMonitorUpdateErrZ.ok()
    }
}
