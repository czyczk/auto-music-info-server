package com.zenasoft.ami.service.googlesearch.model

import com.zenasoft.ami.service.googlesearch.type.SearchEngineEnum
import com.zenasoft.ami.service.googlesearch.type.SearchProviderEnum
import kotlinx.serialization.Serializable

@Serializable
class SearchResult {

    lateinit var provider: SearchProviderEnum

    lateinit var engine: SearchEngineEnum

    lateinit var query: String

    var correctedQuery: String? = null

    var results: List<SearchResultEntry> = listOf()

}