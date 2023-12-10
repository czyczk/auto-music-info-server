package com.zenasoft.ami.integration.perplexityapi.model

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

@Serializable
class ResponseUsage {

    var promptTokens by Delegates.notNull<Int>()

    var completionTokens by Delegates.notNull<Int>()

    var totalTokens by Delegates.notNull<Int>()

}
