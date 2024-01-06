package com.zenasoft.ami.integration.perplexityapi.model.dto

import com.zenasoft.ami.integration.perplexityapi.model.ResponseChoice
import kotlinx.serialization.Serializable

@Serializable
data class ResponseChoiceDto(
    var index: Int,
    var finishReason: String,
    var message: MessageDto,
    var delta: MessageDto,
)

fun ResponseChoiceDto.toModel(): ResponseChoice {
    return ResponseChoice(
        index = index,
        finishReason = finishReason,
        message = message.toModel(),
        delta = delta.toModel(),
    )
}

fun ResponseChoice.Companion.fromDto(dto: ResponseChoiceDto): ResponseChoice {
    return dto.toModel()
}