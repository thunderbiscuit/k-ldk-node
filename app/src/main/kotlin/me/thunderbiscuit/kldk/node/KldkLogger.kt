package me.thunderbiscuit.kldk.node

import org.ldk.structs.Logger
import org.ldk.structs.Record
import mu.KotlinLogging
import org.ldk.enums.Level

private val MuLogger = KotlinLogging.logger("BaseLogger")

// to create a Logger we need to provide an object that implements the LoggerInterface
// which has 1 function: log(record: Record?): Unit
object KldkLogger : Logger.LoggerInterface {
    override fun log(record: Record?) {
        // MuLogger.info { record?.get_args().toString() }
        if (record?.get_level() != Level.LDKLevel_Gossip) {
            MuLogger.info { record?.get_args().toString() }
        }
    }
}
