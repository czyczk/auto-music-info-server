package com.zenasoft.ami.config.appconfig

import kotlinx.serialization.Serializable

@Serializable
class ExternalServiceConfig {

    lateinit var googleCustomSearch: GoogleCustomSearchConfig

    lateinit var perplexityAi: PerplexityAiConfig

}