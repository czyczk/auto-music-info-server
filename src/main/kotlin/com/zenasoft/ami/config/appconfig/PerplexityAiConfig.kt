package com.zenasoft.ami.config.appconfig

import com.zenasoft.ami.config.serializer.EncryptedStringSerializer
import kotlinx.serialization.Serializable

@Serializable
class PerplexityAiConfig {

    @Serializable(with = EncryptedStringSerializer::class)
    lateinit var apiKey: String

}
