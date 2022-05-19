package me.thunderbiscuit.kldk.node

import org.ldk.structs.Logger
import org.ldk.structs.Record

// to create a Logger we need to provide an object that implements the LoggerInterface
// which has 1 function: log(record: Record?): Unit
object KldkLogger : Logger.LoggerInterface {
    override fun log(record: Record?) {
        println("Log entry: ${record?.get_args().toString()}")
    }
}
