package me.thunderbiscuit.kldk

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.thunderbiscuit.kldk.utils.toHex
import me.thunderbiscuit.kldk.utils.toByteArray
import org.ldk.batteries.ChannelManagerConstructor
import org.ldk.batteries.NioPeerHandler
import org.ldk.enums.ConfirmationTarget
import org.ldk.enums.Network
import org.ldk.structs.*
import java.io.File

var nioPeerHandler: NioPeerHandler? = null
var peerManager: PeerManager? = null

// the startNode function fires up the basic things we need for the node to operate
fun startNode() {
    println("Kldk starting...")

    val feeEstimator: FeeEstimator = FeeEstimator.new_impl(KldkFeeEstimator)
    // the other way to provide the interface is using Kotlin SAM conversion

    val logger: Logger = Logger.new_impl(KldkLogger)

    val transactionBroadcaster: BroadcasterInterface = BroadcasterInterface.new_impl(KldkBroadcaster)

    // val network: Network = Network.LDKNetwork_Testnet
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
    val txFilter: Filter = Filter.new_impl(KldkFilter)
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

        // remove non-null assertion operator once the else branch of the variable declaration above is completed
        val channelManager: ChannelManager = channelManagerConstructor?.channel_manager ?: throw IllegalStateException("Channel manager has not been initialized")
        nioPeerHandler = channelManagerConstructor.nio_peer_handler
        peerManager = channelManagerConstructor.peer_manager

    } catch (e: Throwable) {
        println("Kldk startup error: $e")
    }
}

// to create a FeeEstimator we need to provide an object that implement the FeeEstimatorInterface
// which has 1 function: get_est_sat_per_1000_weight(conf_target: ConfirmationTarget?): Int
object KldkFeeEstimator : FeeEstimator.FeeEstimatorInterface {
    override fun get_est_sat_per_1000_weight(confirmation_target: ConfirmationTarget?): Int {
        // we don't actually use the confirmation_target parameter and simply return 25_000 no matter what
        return 25_000
    }
}

// to create a Logger we need to provide an object that implements the LoggerInterface
// which has 1 function: log(record: Record?): Unit
object KldkLogger : Logger.LoggerInterface {
    override fun log(record: Record?) {
        println("Log entry: ${record?.get_args().toString()}")
    }
}

object KldkBroadcaster : BroadcasterInterface.BroadcasterInterfaceInterface {
    override fun broadcast_transaction(tx: ByteArray?): Unit {
        println(tx.toString())
    }
}

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

object KldkEventHandler : ChannelManagerConstructor.EventHandler {
    override fun handle_event(events: Event?) {
        when (events) {
            is Event.FundingGenerationReady -> println("We just had a FundingGenerationReady event")
            is Event.ChannelClosed          -> println("We just had a ChannelClosed event")
            is Event.DiscardFunding         -> println("We just had a DiscardFunding event")
            else                            -> println("We just had a $events event")
        }
    }

    override fun persist_manager(channel_manager_bytes: ByteArray?) {
        println("Persist manager")
        if (channel_manager_bytes != null) {
            val hex = channel_manager_bytes.toHex()
            println("Channel manager bytes: $hex")
            File("${Config.homeDir}/channelmanager").writeText(channel_manager_bytes.toHex())
        }
    }

    override fun persist_network_graph(network_graph: ByteArray?) {
        println("Implement network graph persistence")
    }
}

object KldkFilter : Filter.FilterInterface {
    override fun register_tx(txid: ByteArray?, script_pubkey: ByteArray?) {
        println("Registering the transaction...")
        // TODO("Not yet implemented")
    }

    override fun register_output(output: WatchedOutput?): Option_C2Tuple_usizeTransactionZZ {
        // TODO("Not yet implemented")
        println("Registering the output...")
        return Option_C2Tuple_usizeTransactionZZ.none()
    }
}

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
