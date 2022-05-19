package me.thunderbiscuit.kldk.node

import org.ldk.structs.Filter
import org.ldk.structs.Option_C2Tuple_usizeTransactionZZ
import org.ldk.structs.WatchedOutput

object KldkTransactionFilter : Filter.FilterInterface {
    override fun register_tx(txid: ByteArray?, script_pubkey: ByteArray?) {
        // TODO("Not yet implemented")
        println("Registering the transaction...")
    }

    override fun register_output(output: WatchedOutput?): Option_C2Tuple_usizeTransactionZZ {
        // TODO("Not yet implemented")
        println("Registering the output...")
        return Option_C2Tuple_usizeTransactionZZ.none()
    }
}
