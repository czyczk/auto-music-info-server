package com.zenasoft.ami.integration.googlesearch

import com.zenasoft.ami.integration.googlesearch.model.GoogleSearchOkResponse

interface IGoogleSearchClient {

    suspend fun search(query: String, engineId: String): GoogleSearchOkResponse

}