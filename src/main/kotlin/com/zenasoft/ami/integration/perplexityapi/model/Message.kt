package com.zenasoft.ami.integration.perplexityapi.model

import com.zenasoft.ami.integration.perplexityapi.model.type.RoleEnum
import kotlinx.serialization.Serializable

@Serializable
class Message {

    lateinit var role: RoleEnum

    lateinit var content: String

}
