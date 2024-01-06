package com.zenasoft.ami.integration.perplexityapi.model.dto

import com.zenasoft.ami.integration.perplexityapi.model.Request
import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum
import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
    var model: String,
    var messages: List<MessageDto>,
)

fun Request.toDto(): RequestDto {
    return RequestDto(
        model = this.model.apiCode,
        messages = this.messages.map { it.toDto() },
    )
}

fun RequestDto.toModel(): Request {
    return Request(
        model = ModelEnum.fromApiCode(this.model),
        messages = this.messages.map { it.toModel() }.toList(),
    )
}

fun Request.Companion.fromDto(dto: RequestDto): Request {
    return dto.toModel()
}
