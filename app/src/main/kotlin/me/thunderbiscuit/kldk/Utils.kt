package me.thunderbiscuit.kldk

fun ByteArray.toHex(): String {
    return joinToString(separator = "") { byte ->
        String.format("%02X".format(byte))
    }.lowercase()
}

fun String.toByteArray(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
