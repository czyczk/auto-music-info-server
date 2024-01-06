package com.zenasoft.ami.integration.googlesearch.impl

import com.zenasoft.ami.common.AmiContext
import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import com.zenasoft.ami.integration.googlesearch.IGoogleSearchClient
import com.zenasoft.ami.integration.googlesearch.model.GoogleSearchErrorResponse
import com.zenasoft.ami.integration.googlesearch.model.GoogleSearchOkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URLEncoder

class GoogleSearchClientImpl : KoinComponent, IGoogleSearchClient {

    private val ctx: AmiContext by inject()

    private val jsonKSerializer: Json by inject()

    private val okHttpClient: OkHttpClient by inject()

    private val ktorClient: HttpClient by inject()

    override suspend fun search(query: String, engineId: String): GoogleSearchOkResponse {
        val url = getSearchUrl(
            query = query,
            apiKey = ctx.appConfig.externalService.googleCustomSearch.apiKey,
            engineId = engineId,
        )

        try {
            val response = ktorClient.get(url)
            val responseBody = response.bodyAsText()

            if (response.status.value == 400) {
                val errorResponse = jsonKSerializer.decodeFromString(
                    GoogleSearchErrorResponse.serializer(),
                    responseBody,
                )
                val amiException = AmiException.of(
                    AmiErrorCode.AMI_I001_001,
                    "statusCode: ${response.status.value}; errorCode: ${errorResponse.error.code}; errorMessage: ${errorResponse.error.message}"
                )
                throw amiException
            }

            if (response.status.value != 200) {
                throw AmiException.of(
                    AmiErrorCode.AMI_I001_001,
                    "statusCode: ${response.status.value}; body: $responseBody"
                )
            }

            return jsonKSerializer.decodeFromString(
                GoogleSearchOkResponse.serializer(),
                responseBody,
            )
        } catch (e: Exception) {
            throw AmiException.of(e, AmiErrorCode.AMI_I001_001, e.message ?: "")
        }
    }

    suspend fun searchUsingOkHttpClient(query: String, engineId: String): GoogleSearchOkResponse {
        val url = getSearchUrl(
            query = query,
            apiKey = ctx.appConfig.externalService.googleCustomSearch.apiKey,
            engineId = engineId,
        )

        val request: Request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            val response: Response
            try {
                response = okHttpClient.newCall(request).execute()
            } catch (e: Exception) {
                throw AmiException.of(AmiErrorCode.AMI_I001_001, e.message ?: "")
            }

            if (response.code == 400) {
                val responseBody = response.body!!.string() ?: ""
                val errorResponse = jsonKSerializer.decodeFromString(
                    GoogleSearchErrorResponse.serializer(),
                    responseBody,
                )
                val amiException = AmiException.of(
                    AmiErrorCode.AMI_I001_001,
                    "statusCode: ${response.code}; errorCode: ${errorResponse.error.code}; errorMessage: ${errorResponse.error.message}"
                )
                throw amiException
            }

            if (response.code != 200) {
                throw AmiException.of(
                    AmiErrorCode.AMI_I001_001,
                    "statusCode: ${response.code}; body: ${response.body?.string()}"
                )
            }
            response.body ?: throw AmiException.of(AmiErrorCode.AMI_I001_001, "`response.body` is null")

            return@withContext jsonKSerializer.decodeFromString(
                GoogleSearchOkResponse.serializer(),
                response.body!!.string()
            )
        }
    }

    private fun getSearchUrl(query: String, apiKey: String, engineId: String): String {
        return "https://www.googleapis.com/customsearch/v1?key=$apiKey&cx=$engineId&q=${
            URLEncoder.encode(
                query,
                Charsets.UTF_8,
            )
        }"
    }


}