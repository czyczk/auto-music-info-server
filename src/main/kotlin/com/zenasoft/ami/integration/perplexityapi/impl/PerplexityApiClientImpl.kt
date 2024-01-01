package com.zenasoft.ami.integration.perplexityapi.impl

import com.zenasoft.ami.integration.perplexityapi.IPerplexityApiClient
import com.zenasoft.ami.integration.perplexityapi.model.Request
import com.zenasoft.ami.integration.perplexityapi.model.Response
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

private val url = "https://api.perplexity.ai/chat/completions"

class PerplexityApiClientImpl : KoinComponent, IPerplexityApiClient {

    private val json: Json by inject<Json>(named("perplexityApiJsonKSerializer"))

    private val perplexityApiKtorClient: HttpClient by inject(named("perplexityApiKtorClient"))

    override suspend fun getCompletion(request: Request): Response {
        val httpResponse = perplexityApiKtorClient.post(url) {
            setBody(json.encodeToString(request))
        }

        val response: Response = httpResponse.body()
        return response
    }
}