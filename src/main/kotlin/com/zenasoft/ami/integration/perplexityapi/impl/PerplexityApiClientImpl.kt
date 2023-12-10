package com.zenasoft.ami.integration.perplexityapi.impl

import com.zenasoft.ami.integration.perplexityapi.IPerplexityApiClient
import com.zenasoft.ami.integration.perplexityapi.model.Request
import com.zenasoft.ami.integration.perplexityapi.model.Response
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

private val url = "https://api.perplexity.ai/chat/completions"

class PerplexityApiClientImpl : KoinComponent, IPerplexityApiClient {

    private val json: Json by inject<Json>(named("perplexityApiJsonKSerializer"))

    private val okHttpClient: OkHttpClient by inject()

    override suspend fun getCompletion(request: Request, apiKey: String): Response {
        // Make a new OkHttpClient instance with "authorization: Bearer ${apiKey}" in header.
        val okHttpClientAuthorized = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                val requestWithApiKey = chain.request().newBuilder()
                    .addHeader("authorization", "Bearer ${apiKey}")
                    .build()
                chain.proceed(requestWithApiKey)
            }
            .build()
    }
}