package me.thunderbiscuit.kldk

import me.thunderbiscuit.kldk.node.*
import me.thunderbiscuit.kldk.utils.Config
import me.thunderbiscuit.kldk.utils.toByteArray
import me.thunderbiscuit.kldk.utils.toHex
import org.bitcoindevkit.DescriptorSecretKey
import org.ldk.batteries.ChannelManagerConstructor
import org.ldk.batteries.NioPeerHandler
import org.ldk.enums.Network
import org.ldk.structs.*
import java.io.File

// the Node singleton is really just a way to namespace and keep references to
// the peerHandler, peerManager, channelManager, and channelManagerConstructor
// so we can use them throughout the app, and they don't get garbage collected
object Node {

    lateinit var peerHandler: NioPeerHandler
    lateinit var peerManager: PeerManager
    lateinit var channelManager: ChannelManager
    private lateinit var channelManagerConstructor: ChannelManagerConstructor

    // the init block gets run only once upon initialization of the object
    // the startNode method sets the 4 lateinit variables above
    init {
        startNode()
    }

    fun initialize() {
        return Unit
    }

    private fun startNode() {

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
        val networkGraph = NetworkGraph.of((Config.genesisHash.toByteArray()).reversedArray(), logger)

        val persister: Persist = Persist.new_impl(KldkPersister)

        val eventHandler: ChannelManagerConstructor.EventHandler = KldkEventHandler

        val serializedChannelMonitorsList: MutableList<String> = mutableListOf()
        var serializedChannelManager: String? = null
        File(Config.homeDir).mkdir()
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

        // val handshake = ChannelHandshakeConfig.with_default()
        // handshake._announced_channel = true
        val userConfig: UserConfig = UserConfig.with_default()
        // userConfig._channel_handshake_config = handshake

        // val transactionFilter: Option_FilterZ? = null
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

        // val scorer: MultiThreadedLockableScore = MultiThreadedLockableScore.of(Scorer.with_default().as_Score())
        val scorerParameters: ProbabilisticScoringParameters = ProbabilisticScoringParameters.with_default()
        val probabilisticScorer: ProbabilisticScorer = ProbabilisticScorer.of(scorerParameters, networkGraph, logger)
        val scorer: MultiThreadedLockableScore = MultiThreadedLockableScore.of(probabilisticScorer.as_Score())

        val entropy: ByteArray = getStartingEntropy(Config.mnemonic)
        val keysManager: KeysManager = KeysManager.of(
            entropy,
            System.currentTimeMillis() / 1000,
            (System.currentTimeMillis() * 1000).toInt()
        )

        // Create the 4 variables we need in the Node object
        // channelManagerConstructor, channelManager, peerHandler, peerManager
        try {
            this.channelManagerConstructor = when (serializedChannelManager) {
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
                    throw(IllegalStateException("Reboot currently not supported"))
                }
            }
            this.channelManager = channelManagerConstructor.channel_manager
            this.peerHandler = channelManagerConstructor.nio_peer_handler
            this.peerManager = channelManagerConstructor.peer_manager

            channelManagerConstructor.chain_sync_completed(eventHandler, scorer)

            // val bestHeader: ByteArray = runBlocking {
            //     getLatestBlockHeader()
            // }
            // val bestHeight: Int = runBlocking {
            //     getLatestBlockHeight()
            // }
            // channelManager.update_best_block(best_header, best_height)
            // channelManager.current_best_block()
            // channelManager.best_block_updated()
            // chainMonitor.update_best_block(best_header, best_height)
            // chainMonitor.best_block_updated()
            // channelManager.as_Confirm()

        } catch (e: Throwable) {
            println("Kldk startup error: $e")
            throw e
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun getStartingEntropy(mnemonic: String): ByteArray {
         val entropy: ByteArray = DescriptorSecretKey(
             network = org.bitcoindevkit.Network.TESTNET,
             mnemonic = mnemonic,
             password = null,
        ).secretBytes().toUByteArray().toByteArray()

        println("Entropy used for LDK is ${entropy.toHex()}")
        return entropy
    }
}
