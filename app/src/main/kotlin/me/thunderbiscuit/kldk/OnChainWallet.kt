package me.thunderbiscuit.kldk

import me.thunderbiscuit.kldk.utils.Config
import me.thunderbiscuit.kldk.utils.toHex
import org.bitcoindevkit.*
import org.bitcoindevkit.Wallet

object OnChainWallet {
    private lateinit var wallet: Wallet
    private lateinit var blockchain: Blockchain

    init {
        val electrumURL: String = Config.electrumUrl
        val blockchainConfig = BlockchainConfig.Electrum(ElectrumConfig(electrumURL, null, 10u, 20u, 10u))
        blockchain = Blockchain(blockchainConfig)
        val bip32RootKey: DescriptorSecretKey = DescriptorSecretKey(
            network = Network.TESTNET,
            mnemonic = Config.mnemonic,
            password = ""
        )
        wallet = Wallet(
            descriptor = createExternalDescriptor(bip32RootKey),
            changeDescriptor = createInternalDescriptor(bip32RootKey),
            network = Network.TESTNET,
            databaseConfig = DatabaseConfig.Sqlite(SqliteDbConfiguration("bdk-onchain-sqlite")),
        )
    }

    object LogProgress: Progress {
        override fun update(progress: Float, message: String?) {
            println("Sync onchain wallet")
        }
    }

    private fun createExternalDescriptor(rootKey: DescriptorSecretKey): String {
        val externalPath: DerivationPath = DerivationPath("m/84h/1h/0h/0")
        return "wpkh(${rootKey.extend(externalPath).asString()})"
    }

    private fun createInternalDescriptor(rootKey: DescriptorSecretKey): String {
        val externalPath: DerivationPath = DerivationPath("m/84h/1h/0h/1")
        return "wpkh(${rootKey.extend(externalPath).asString()})"
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun buildFundingTx(value: Long, script: ByteArray): ByteArray {
        wallet.sync(blockchain, LogProgress)
        val scriptListUByte: List<UByte> = script.toUByteArray().asList()
        val outputScript: Script = Script(scriptListUByte)
        val (psbt, txDetails) = TxBuilder()
            .addRecipient(outputScript, value.toULong())
            .feeRate(4.0F)
            .finish(wallet)
        wallet.sign(psbt)
        val rawTx = psbt.extractTx().toUByteArray().toByteArray()
        println("The raw funding tx is ${rawTx.toHex()}")
        return rawTx
    }
}
