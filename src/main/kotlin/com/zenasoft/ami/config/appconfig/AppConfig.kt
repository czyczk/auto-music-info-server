package com.zenasoft.ami.config.appconfig

import kotlinx.serialization.Serializable

@Serializable
class AppConfig {

    lateinit var network: NetworkConfig

    lateinit var externalService: ExternalServiceConfig

}