package me.thunderbiscuit.kldk

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.runBlocking
import me.thunderbiscuit.kldk.network.connectPeer
import me.thunderbiscuit.kldk.utils.listPeers
import java.io.File
import kotlin.system.exitProcess

fun main() {
    println("Hello, ${Config.nodeName}!\n")

    while (true) {
        print("Kldk ❯❯❯ ")
        val input: List<String> = readln().split(" ")

        try {
            Kldk()
                .subcommands(
                    StartNode(),
                    ConnectToPeer(),
                    ListPeers(),
                    TestCommand(),
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
        }
        catch (e: Throwable) {
            println("ERROR: ${e.printStackTrace()}")
            println()
        }
    }
}

class Kldk : CliktCommand() {
    override val commandHelp = """
        Kldk is a lightning node.

        Call any of the commands to interact with it.
    """

    override fun run() = Unit
}

class StartNode : CliktCommand(help = "Start your Kldk node", name = "startnode") {
    override fun run() {
        startNode()
    }
}

class ConnectToPeer : CliktCommand(help = "Connect to a peer", name = "connectpeer") {
    private val pubkey by option("--pubkey", help="Peer public key (required)").required()
    private val ip by option("--ip", help="Peer ip address (required)").required()
    private val port by option("--port", help="Peer port (required)").int().required()
    override fun run() {
        echo("Kldk attempting to connect to peer $pubkey@$ip:$port...")
        connectPeer(
            pubkey = pubkey,
            hostname = ip,
            port = port
        )
    }
}

class ListPeers : CliktCommand(name = "listpeers") {
    override fun run() {
        echo(listPeers())
    }
}

class TestCommand : CliktCommand(name = "testcommand") {
    override fun run() {
        runBlocking {
            val latestBlockHash = getLatestBlockHash()
            val latestBlockHeight = getLatestBlockHeight()
            echo("Latest block hash is $latestBlockHash")
            echo("Latest block height is $latestBlockHeight")
        }
    }
}

class Shutdown : CliktCommand(help = "Shutdown node") {
    override fun run() {
        echo("Shutting down node...")
    }
}

class Exit : CliktCommand(help = "Exit REPL") {
    override fun run() {
        echo("Exiting the REPL...")
        exitProcess(0)
    }
}
