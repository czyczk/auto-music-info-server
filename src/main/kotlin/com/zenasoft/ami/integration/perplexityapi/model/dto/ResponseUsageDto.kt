package com.zenasoft.ami.integration.perplexityapi.model.dto

import com.zenasoft.ami.integration.perplexityapi.model.ResponseUsage
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUsageDto(
    var promptTokens: Int,
    var completionTokens: Int,
    var totalTokens: Int,
)

fun ResponseUsageDto.toModel(): ResponseUsage {
    return ResponseUsage(
        promptTokens = promptTokens,
        completionTokens = completionTokens,
        totalTokens = totalTokens,
    )
}

fun ResponseUsage.Companion.fromDto(dto: ResponseUsageDto): ResponseUsage {
    return dto.toModel()
}
