package me.thunderbiscuit.kldk

fun byteArrayToHex(bytesArg: ByteArray): String {
    return bytesArg.joinToString("") {
        String.format("%02X", (it.toInt() and 0xFF))
    }.lowercase()
}
