package me.thunderbiscuit.kldk.utils

import com.google.common.io.BaseEncoding

fun ByteArray.toHex(): String {
    return BaseEncoding.base16().encode(this).lowercase()
}

fun String.toByteArray(): ByteArray {
    return BaseEncoding.base16().decode(this.uppercase())
}
