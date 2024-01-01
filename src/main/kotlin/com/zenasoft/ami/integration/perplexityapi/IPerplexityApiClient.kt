package com.zenasoft.ami.integration.perplexityapi

import com.zenasoft.ami.integration.perplexityapi.model.Request
import com.zenasoft.ami.integration.perplexityapi.model.Response

interface IPerplexityApiClient {

    suspend fun getCompletion(request: Request): Response

}