package me.thunderbiscuit.kldk.utils

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import me.thunderbiscuit.kldk.network.getLatestBlockHash
import me.thunderbiscuit.kldk.network.getLatestBlockHeight

object Config {
    private val config: com.typesafe.config.Config = ConfigFactory.load()

    val nodeName: String = config.getString("kldk.nodeName")
    val homeDir: String = config.getString("kldk.homeDir")
    val entropy: String = config.getString("kldk.entropy")
    val genesisHash: String = config.getString("kldk.genesisHash")
    val network: String = config.getString("kldk.network")

    // You can define latestBlockHash and latestBlockHeight in the config file
    // I used these initially because I was going to use regtest and didn't implement an API call to get them
    // val latestBlockHash: String = config.getString("kldk.latestBlockHash")
    // val latestBlockHeight: Int = config.getInt("kldk.latestBlockHeight")

    // do you only need the latestBlockHash/Height once on startup?
    // or is that a value you need to be able to use often and be up-to-date?
    val latestBlockHash: String
        get() = runBlocking { getLatestBlockHash() }
    val latestBlockHeight: Int
        get() = runBlocking { getLatestBlockHeight() }
}
