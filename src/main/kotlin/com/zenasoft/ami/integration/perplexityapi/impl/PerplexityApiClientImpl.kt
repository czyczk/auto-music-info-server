package com.zenasoft.ami.integration.perplexityapi.impl

import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import com.zenasoft.ami.integration.perplexityapi.IPerplexityApiClient
import com.zenasoft.ami.integration.perplexityapi.model.Request
import com.zenasoft.ami.integration.perplexityapi.model.Response
import com.zenasoft.ami.integration.perplexityapi.model.dto.ResponseDto
import com.zenasoft.ami.integration.perplexityapi.model.dto.fromDto
import com.zenasoft.ami.integration.perplexityapi.model.dto.toDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

private const val url = "https://api.perplexity.ai/chat/completions"

class PerplexityApiClientImpl : KoinComponent, IPerplexityApiClient {

    private val jsonKSerializer: Json by inject<Json>(named("perplexityApiJsonKSerializer"))

    private val perplexityApiKtorClient: HttpClient by inject(named("perplexityApiKtorClient"))

    override suspend fun getCompletion(request: Request): Response {
        try {
            val httpResponse = perplexityApiKtorClient.post(url) {
                contentType(ContentType.Application.Json)
                val requestDto = request.toDto()
                setBody(jsonKSerializer.encodeToString(requestDto))
            }
            val httpResponseBody = httpResponse.bodyAsText()

            if (httpResponse.status.value != 200) {
                throw AmiException.of(
                    AmiErrorCode.AMI_I002_001,
                    "statusCode: ${httpResponse.status.value}; body: $httpResponseBody"
                )
            }

            val responseDto = jsonKSerializer.decodeFromString(
                ResponseDto.serializer(),
                httpResponseBody,
            )
            return Response.fromDto(responseDto)
        } catch (e: Exception) {
            throw AmiException.of(e, AmiErrorCode.AMI_I002_001, e.message ?: "")
        }
    }
}