package me.thunderbiscuit.kldk

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.mordant.rendering.OverflowWrap
import kotlinx.coroutines.runBlocking
import me.thunderbiscuit.kldk.utils.Config
import me.thunderbiscuit.kldk.utils.listPeers
import me.thunderbiscuit.kldk.utils.toByteArray
import org.ldk.structs.Result__u832APIErrorZ
import kotlin.system.exitProcess
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import me.thunderbiscuit.kldk.network.*
import me.thunderbiscuit.kldk.utils.getNodeId
import java.io.File

fun main() {
    println(green("Kldk starting..."))
    val node: Node = Node
    println(green("Up and running!\n"))

    while (true) {
        print("Kldk ❯❯❯ ")
        val input: List<String> = readln().split(" ")

        try {
            Kldk()
                .subcommands(
                    Help(),
                    ConnectToPeer(),
                    ListPeers(),
                    OpenChannelPart1(),
                    OpenChannelPart2(),
                    GetBlockInfo(),
                    GetNodeInfo(),
                    Shutdown(),
                    Exit()
                )
                .parse(input)
            println()
        } catch (e: PrintHelpMessage) {
            echo(e.command.getFormattedHelp())
            println()
        } catch (e: UsageError) {
            echo(e.helpMessage())
            println()
        } catch (e: Throwable) {
            println("ERROR: ${e.printStackTrace()}")
            println()
        }
    }
}

class Kldk : CliktCommand() {
    override val commandHelp = """
        Kldk is a bitcoin lightning node ⚡.

        Use any of the commands to interact with it.
    """

    override fun getFormattedHelp(): String {
        val formattedHelp =  super.getFormattedHelp()
        return green(formattedHelp)
    }

    override fun run() = Unit
}

class Help : CliktCommand(name = "help", help = "Print help output") {
    override fun run() {
        echo(green(rootHelpMessage.trimMargin("|")))
    }
}

class ConnectToPeer() : CliktCommand(name = "connectpeer", help = "Connect to a peer") {
    private val pubkey by option("--pubkey", help="Peer public key (required)").required()
    private val ip by option("--ip", help="Peer ip address (required)").required()
    private val port by option("--port", help="Peer port (required)").int().required()

    override fun run() {
        val peer: String = "$pubkey@$ip:$port"
        echo(green("Kldk attempting to connect to peer ") + green(peer))
        val message = connectPeer(
            pubkey = pubkey,
            hostname = ip,
            port = port
        )
        echo(green(message))
    }
}

class ListPeers() : CliktCommand(name = "listpeers", help = "Print a list of connected peers") {
    private val terminal = Terminal()

    override fun run() {
        val peers: List<String> = listPeers()
        when {
            peers.isEmpty() -> echo(green("No connected peers at the moment"))
            else -> terminal.println(
                table {
                    // borderStyle = gray
                    column(1) {
                        // width = ColumnWidth.Expand()
                        overflowWrap = OverflowWrap.BREAK_WORD
                    }
                    header { row("Peer # ", "Node ID") }
                    body {
                        peers.forEachIndexed { index, peerPubkey ->
                            row("Peer ${index + 1} ", white(peerPubkey))
                        }
                    }
                }
            )
        }
    }
}

class OpenChannelPart1() : CliktCommand(name = "openchannel1", help = "Open a channel part 1") {
    private val pubkey by option("--pubkey", help = "Peer public key (required)").required()
    private val channelValue by option("--channelvalue", help = "Channel value in millisatoshi (required)").long().required()
    private val pushAmount by option("--pushamount", help = "Amount to push to the counterparty as part of the open, in millisatoshi (required)").long().required()
    private val userChannelId by option("--userchannelID", help = "User channel ID (required)").long().required()

    override fun run() {
        val response = createFundingTx(
            pubkey = pubkey.toByteArray(),
            channelValue = channelValue,
            pushAmount = pushAmount,
            userChannelId = userChannelId
        )
        when (response) {
            is Result__u832APIErrorZ.Result__u832APIErrorZ_OK -> echo("Channel open request was sent.")
            is Result__u832APIErrorZ.Result__u832APIErrorZ_Err -> echo("Channel open request was not sent. Error is ${response.err}")
        }
    }
}

class OpenChannelPart2() : CliktCommand(name = "openchannel2", help = "Broadcast the funding transaction") {
    private val tempChannelId: String by option("--tempchannelID", help = "Temporary channel id").required()

    override fun run() {
        val tx: String = File("${Config.homeDir}/tx.txt").absoluteFile.readText(Charsets.UTF_8)
        broadcastFundingTx(
            tempChannelId = tempChannelId,
            fundingTx = tx
        )
    }
}

class GetBlockInfo : CliktCommand(name = "getblockinfo", help = "Print latest block height and hash") {
    override fun run() {
        runBlocking {
            val latestBlockHash = getLatestBlockHash()
            val latestBlockHeight = getLatestBlockHeight()
            echo(green("Latest block hash is $latestBlockHash"))
            echo(green("Latest block height is $latestBlockHeight"))
        }
    }
}

class GetNodeInfo() : CliktCommand(name = "getnodeinfo", help = "Print node information") {
    private val terminal: Terminal = Terminal()
    private val nodeId: String = getNodeId()
    private val status: String = green("⬤  Up and running")

    override fun run() {
        terminal.println(
            table {
                column(1) {
                    overflowWrap = OverflowWrap.BREAK_WORD
                }
                // header { row("Data", "Info") }
                body {
                    row("Node Status", status)
                    row("Network", Config.network.replaceFirstChar { it.uppercase() })
                    row("Node ID", nodeId)
                }
            }
        )
    }
}

class Shutdown : CliktCommand(name = "shutdown", help = "Shutdown node") {
    override fun run() {
        echo(green("Shutting down node... (this has not been implemented yet)"))
    }
}

class Exit : CliktCommand(name = "exit", help = "Exit REPL") {
    override fun run() {
        echo(green("Exiting the REPL..."))
        exitProcess(0)
    }
}

const val rootHelpMessage = """
    |Usage: COMMAND [ARGS]...
    |
    |  Kldk is a lightning node ⚡.
    |
    |  Use any of the commands to interact with it.
    |
    |Commands:
    |  help          Print the root help output
    |  connectpeer   Connect to a peer
    |  listpeers     Print a list of connected peers
    |  getblockinfo  Print latest block height and hash
    |  openchannel1  Negotiate the funding transaction with a peer
    |  openchannel2  Broadcast the funding transaction
    |  getnodeinfo   Print node information
    |  shutdown      Shutdown node
    |  exit          Exit REPL"""
