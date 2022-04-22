package me.thunderbiscuit.kldk

import com.typesafe.config.ConfigFactory

object Config {
    private val config: com.typesafe.config.Config = ConfigFactory.load()

    val me: String = config.getString("kldk.name")
    val homeDir: String = config.getString("homeDir")
}
