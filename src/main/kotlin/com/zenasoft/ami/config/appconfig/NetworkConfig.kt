package com.zenasoft.ami.config.appconfig

import kotlinx.serialization.Serializable

@Serializable
class NetworkConfig {

    var port: Int = 8080

    var proxyUrl: String? = null

}