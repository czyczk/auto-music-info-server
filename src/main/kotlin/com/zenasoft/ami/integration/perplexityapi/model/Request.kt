package com.zenasoft.ami.integration.perplexityapi.model

import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum
import kotlinx.serialization.Serializable

@Serializable
class Request {

    lateinit var model: ModelEnum

    lateinit var messages: List<Message>

}