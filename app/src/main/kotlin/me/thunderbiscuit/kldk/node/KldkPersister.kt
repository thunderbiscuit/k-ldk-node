package me.thunderbiscuit.kldk.node

import me.thunderbiscuit.kldk.Config
import me.thunderbiscuit.kldk.utils.toHex
import org.ldk.structs.*
import java.io.File

object KldkPersister : Persist.PersistInterface {
    override fun persist_new_channel(
        channel_id: OutPoint?,
        data: ChannelMonitor?,
        update_id: MonitorUpdateId?
    ): Result_NoneChannelMonitorUpdateErrZ? {
        if (channel_id == null || data == null) return null
        val channelMonitorBytes: ByteArray = data.write()
        File("${Config.homeDir}/channelmonitor${channel_id.to_channel_id().toHex()}.hex")
            .writeText(channelMonitorBytes.toHex())
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
        File("${Config.homeDir}/channelmonitor${channel_id.to_channel_id().toHex()}.hex")
            .writeText(channelMonitorBytes.toHex())
        return Result_NoneChannelMonitorUpdateErrZ.ok()
    }
}
