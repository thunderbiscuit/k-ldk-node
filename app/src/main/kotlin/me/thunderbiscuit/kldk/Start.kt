package me.thunderbiscuit.kldk

import me.thunderbiscuit.kldk.node.*
import me.thunderbiscuit.kldk.utils.Config
import me.thunderbiscuit.kldk.utils.toByteArray
import org.ldk.batteries.ChannelManagerConstructor
import org.ldk.batteries.NioPeerHandler
import org.ldk.enums.Network
import org.ldk.structs.*
import java.io.File

var nioPeerHandler: NioPeerHandler? = null
var peerManager: PeerManager? = null

fun startNode() {

    val feeEstimator: FeeEstimator = FeeEstimator.new_impl(KldkFeeEstimator)
    // The other way to provide the interface is by using Kotlin SAM conversion
    // See the docs here: https://kotlinlang.org/docs/fun-interfaces.html
    // Using Kotlin SAM conversion, you could initialize your feeEstimator like so:
    // val feeEstimator: FeeEstimator = FeeEstimator.new_impl {
    //     return@new_impl 25_000
    // }

    val logger: Logger = Logger.new_impl(KldkLogger)

    val transactionBroadcaster: BroadcasterInterface = BroadcasterInterface.new_impl(KldkBroadcaster)

    val network: Network = if (Config.network == "regtest") Network.LDKNetwork_Regtest else Network.LDKNetwork_Testnet
    val networkGraph = NetworkGraph.of((Config.genesisHash.toByteArray()).reversedArray())

    val persister: Persist = Persist.new_impl(KldkPersister)

    val eventHandler: ChannelManagerConstructor.EventHandler = KldkEventHandler

    val serializedChannelMonitorsList: MutableList<String> = mutableListOf()
    var serializedChannelManager: String? = null
    File(Config.homeDir).walk().forEach {
        if (it.name.startsWith("channelmonitor")) {
            val serializedMonitor: String = it.absoluteFile.readText(Charsets.UTF_8)
            serializedChannelMonitorsList.add(serializedMonitor)
        }
        if (it.name.startsWith("channelmanager")) {
            serializedChannelManager = it.absoluteFile.readText(Charsets.UTF_8)
        }
    }

    val channelMonitorsList: ArrayList<ByteArray> = arrayListOf<ByteArray>()
    serializedChannelMonitorsList.forEach {
        val channelMonitorBytes: ByteArray = it.toByteArray()
        channelMonitorsList.add(channelMonitorBytes)
    }
    val channelMonitors: Array<ByteArray> = channelMonitorsList.toTypedArray()

    val userConfig: UserConfig = UserConfig.with_default()

    // val filter: Option_FilterZ? = null
    // val filter: Option_FilterZ = Option_FilterZ.some(txFilter)
    val txFilter: Filter = Filter.new_impl(KldkTransactionFilter)
    val transactionFilter = Option_FilterZ.some(txFilter)
    val chainMonitor: ChainMonitor = ChainMonitor.of(
        transactionFilter,
        transactionBroadcaster,
        logger,
        feeEstimator,
        persister
    )

    val entropy: String = Config.entropy
    val keysManager: KeysManager = KeysManager.of(
        entropy.toByteArray(),
        System.currentTimeMillis() / 1000,
        (System.currentTimeMillis() * 1000).toInt()
    )

    try {
        val channelManagerConstructor: ChannelManagerConstructor? = when (serializedChannelManager) {
            // first time booting up the node
            null -> ChannelManagerConstructor(
                        network,
                        userConfig,
                        Config.latestBlockHash.toByteArray(),
                        Config.latestBlockHeight,
                        keysManager.as_KeysInterface(),
                        feeEstimator,
                        chainMonitor,
                        null,
                        transactionBroadcaster,
                        logger
                    )
            // loading previous state from disk
            else -> {
                println("Node is not on its first boot")
                null
            }
        }

        val channelManager: ChannelManager = channelManagerConstructor?.channel_manager ?: throw IllegalStateException("Channel manager has not been initialized")
        nioPeerHandler = channelManagerConstructor.nio_peer_handler
        peerManager = channelManagerConstructor.peer_manager

    } catch (e: Throwable) {
        println("Kldk startup error: $e")
    }
}
