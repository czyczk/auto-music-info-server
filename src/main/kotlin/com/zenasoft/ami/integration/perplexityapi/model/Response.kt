package com.zenasoft.ami.integration.perplexityapi.model

import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum
import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

@Serializable
class Response {

    lateinit var id: String

    lateinit var model: ModelEnum

    lateinit var `object`: String

    var created by Delegates.notNull<Long>()

    lateinit var usage: ResponseUsage

    lateinit var choices: List<ResponseChoice>

}