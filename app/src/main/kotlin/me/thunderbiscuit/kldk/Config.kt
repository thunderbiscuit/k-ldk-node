package me.thunderbiscuit.kldk

import com.typesafe.config.ConfigFactory

object Config {
    private val config: com.typesafe.config.Config = ConfigFactory.load()

    val nodeName: String = config.getString("kldk.nodeName")
    val homeDir: String = config.getString("kldk.homeDir")
}
