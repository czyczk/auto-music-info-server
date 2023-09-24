package com.zenasoft.ami.service.googlesearch

import com.zenasoft.ami.service.googlesearch.model.SearchResult
import com.zenasoft.ami.service.googlesearch.type.SearchEngineEnum

interface IGoogleSearchService {
    suspend fun search(query: String, engineEnum: SearchEngineEnum): SearchResult
}