package com.zenasoft.ami.integration.perplexityapi.model.dto

import com.zenasoft.ami.integration.perplexityapi.model.Response
import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    var id: String,
    var model: String,
    var `object`: String,
    var created: Long,
    var usage: ResponseUsageDto,
    var choices: List<ResponseChoiceDto>,
)

fun ResponseDto.toModel(): Response {
    return Response(
        id = id,
        model = ModelEnum.fromApiCode(model),
        `object` = `object`,
        created = created,
        usage = usage.toModel(),
        choices = choices.map { it.toModel() },
    )
}

fun Response.Companion.fromDto(dto: ResponseDto): Response {
    return dto.toModel()
}
