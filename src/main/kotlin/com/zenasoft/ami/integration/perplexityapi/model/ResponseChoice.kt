package com.zenasoft.ami.integration.perplexityapi.model

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

@Serializable
class ResponseChoice {

    var index by Delegates.notNull<Int>()

    lateinit var finishReason: String

    lateinit var message: Message

    lateinit var delta: Message

}
