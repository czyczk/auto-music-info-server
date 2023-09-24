package com.zenasoft.ami.config.appconfig

import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI

class NetworkConfigUtil {

    companion object {

        fun createProxyFromUrl(proxyUrl: String): Proxy {
            val uri = URI.create(proxyUrl)
            val proxyType = when (uri.scheme) {
                "http", "https" -> Proxy.Type.HTTP
                "socks5" -> Proxy.Type.SOCKS
                else -> throw IllegalArgumentException("Unsupported proxy scheme: ${uri.scheme}")
            }

            return Proxy(proxyType, InetSocketAddress(uri.host, uri.port))
        }

    }
}