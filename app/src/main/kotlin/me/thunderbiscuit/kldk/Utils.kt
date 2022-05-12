package me.thunderbiscuit.kldk

fun byteArrayToHex(bytesArg: ByteArray): String {
    return bytesArg.joinToString("") {
        String.format("%02X", (it.toInt() and 0xFF))
    }.lowercase()
}

fun String.hexStringToByteArray(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
