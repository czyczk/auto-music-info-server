package com.zenasoft.ami.integration.perplexityapi.model.dto

import com.zenasoft.ami.integration.perplexityapi.model.Message
import com.zenasoft.ami.integration.perplexityapi.model.type.RoleEnum
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    var role: String,
    var content: String,
)

fun Message.toDto(): MessageDto {
    return MessageDto(
        role = this.role.apiCode,
        content = this.content,
    )
}

fun MessageDto.toModel(): Message {
    return Message(
        role = RoleEnum.fromApiCode(this.role),
        content = this.content,
    )
}

fun Message.Companion.fromDto(dto: MessageDto): Message {
    return dto.toModel()
}
