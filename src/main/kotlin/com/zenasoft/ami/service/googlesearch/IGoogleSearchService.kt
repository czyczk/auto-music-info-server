package com.zenasoft.ami.service.googlesearch

import com.zenasoft.ami.service.googlesearch.model.SearchResult
import com.zenasoft.ami.service.googlesearch.model.type.SearchEngineEnum

interface IGoogleSearchService {
    suspend fun search(query: String, engineEnum: SearchEngineEnum): SearchResult

    suspend fun searchIntegrated(
        query: String,
        mainEngineEnums: List<SearchEngineEnum>,
        restEngineEnum: SearchEngineEnum?,
    ): Map<SearchEngineEnum, SearchResult>

}