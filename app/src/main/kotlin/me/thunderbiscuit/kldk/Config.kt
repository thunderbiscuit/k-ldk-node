package me.thunderbiscuit.kldk

import com.typesafe.config.ConfigFactory

object Config {
    private val config: com.typesafe.config.Config = ConfigFactory.load()

    val nodeName: String = config.getString("kldk.nodeName")
    val homeDir: String = config.getString("kldk.homeDir")
    val entropy: String = config.getString("kldk.entropy")
    val latestBlockHash: String = config.getString("kldk.latestBlockHash")
    val latestBlockHeight: Int = config.getInt("kldk.latestBlockHeight")
    val genesisHash: String = config.getString("kldk.genesisHash")
    val network: String = config.getString("kldk.network")
}
