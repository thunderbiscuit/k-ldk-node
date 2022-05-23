package me.thunderbiscuit.kldk.utils

import com.google.common.io.BaseEncoding

// fun ByteArray.toHex(): String {
//     return joinToString(separator = "") { byte ->
//         String.format("%02X".format(byte))
//     }.lowercase()
// }
//
// fun String.toByteArray(): ByteArray {
//     check(length % 2 == 0) { "Must have an even length" }
//
//     return chunked(2)
//                .map { it.toInt(16).toByte() }
//                .toByteArray()
// }

fun ByteArray.toHex(): String {
    return BaseEncoding.base16().encode(this).lowercase()
}

fun String.toByteArray(): ByteArray {
    return BaseEncoding.base16().decode(this.uppercase())
}
