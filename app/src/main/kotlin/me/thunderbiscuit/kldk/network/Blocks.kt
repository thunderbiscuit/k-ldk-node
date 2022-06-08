package me.thunderbiscuit.kldk.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.thunderbiscuit.kldk.utils.toHex

// Blockstream API docs at https://github.com/Blockstream/esplora/blob/master/API.md

// All those calls should eventually be performed using the Bitcoin Dev Kit, which would
// enable more flexibility (choice of bitcoin daemon, electrum server, etc.)

suspend fun getLatestBlockHash(): String {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://blockstream.info/testnet/api/blocks/tip/hash")
    return response.body()
}

suspend fun getLatestBlockHeight(): Int {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://blockstream.info/testnet/api/blocks/tip/height")
    return response.body()
}

suspend fun getLatestBlockHeader(): ByteArray {
    val client = HttpClient(CIO)
    val blockHash: String = getLatestBlockHash()
    val response: HttpResponse = client.get("https://blockstream.info/testnet/api/block/$blockHash/header")
    return response.body<String>().toByteArray()
}

suspend fun blockstreamBroadcastTx(tx: ByteArray): String {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("https://blockstream.info/testnet/api/tx/${tx.toHex()}")
    return response.body<String>()
}
