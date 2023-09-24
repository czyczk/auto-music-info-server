package com.zenasoft.ami.integration.googlesearch.impl

import com.zenasoft.ami.common.AmiContext
import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import com.zenasoft.ami.integration.googlesearch.IGoogleSearchClient
import com.zenasoft.ami.integration.googlesearch.model.GoogleSearchErrorResponse
import com.zenasoft.ami.integration.googlesearch.model.GoogleSearchOkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GoogleSearchClientImpl : KoinComponent, IGoogleSearchClient {

    private val ctx: AmiContext by inject()

    private val jsonKSerializer: Json by inject()

    private val okHttpClient: OkHttpClient by inject()

    override suspend fun search(query: String, engineId: String): GoogleSearchOkResponse {
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
                throw AmiException(AmiErrorCode.AMI_I001_001, e.message ?: "")
            }

            if (response.code == 400) {
                val responseBody = response.body!!.string() ?: ""
                val errorResponse = jsonKSerializer.decodeFromString(
                    GoogleSearchErrorResponse.serializer(),
                    responseBody,
                )
                val amiException = AmiException(
                    AmiErrorCode.AMI_I001_001,
                    "statusCode: ${response.code}; errorCode: ${errorResponse.error.code}; errorMessage: ${errorResponse.error.message}"
                )
                throw amiException
            }

            if (response.code != 200) {
                throw AmiException(
                    AmiErrorCode.AMI_I001_001,
                    "statusCode: ${response.code}; body: ${response.body?.string()}"
                )
            }
            response.body ?: throw AmiException(AmiErrorCode.AMI_I001_001, "`response.body` is null")

            return@withContext jsonKSerializer.decodeFromString(
                GoogleSearchOkResponse.serializer(),
                response.body!!.string()
            )
        }
    }

    private fun getSearchUrl(query: String, apiKey: String, engineId: String): String {
        return "https://www.googleapis.com/customsearch/v1?key=$apiKey&cx=$engineId&q=$query"
    }


}