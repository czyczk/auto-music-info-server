package com.zenasoft.ami.service.googlesearch.impl

import com.zenasoft.ami.common.AmiContext
import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import com.zenasoft.ami.integration.googlesearch.IGoogleSearchClient
import com.zenasoft.ami.service.googlesearch.IGoogleSearchService
import com.zenasoft.ami.service.googlesearch.model.SearchResult
import com.zenasoft.ami.service.googlesearch.model.SearchResultEntry
import com.zenasoft.ami.service.googlesearch.type.SearchEngineEnum
import com.zenasoft.ami.service.googlesearch.type.SearchProviderEnum
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GoogleSearchServiceImpl : KoinComponent, IGoogleSearchService {

    private val logger = KotlinLogging.logger { }

    private val ctx: AmiContext by inject()

    private val googleSearchClient: IGoogleSearchClient by inject()

    private val engineIdMap: Map<SearchEngineEnum, String> = mapOf(
        SearchEngineEnum.ENTIRE to ctx.appConfig.externalService.googleCustomSearch.engineId.entire,
        SearchEngineEnum.WIKIPEDIA to ctx.appConfig.externalService.googleCustomSearch.engineId.wikipedia,
        SearchEngineEnum.MUSIC_PLATFORM to ctx.appConfig.externalService.googleCustomSearch.engineId.musicPlatform,
    )

    override suspend fun search(query: String, engineEnum: SearchEngineEnum): SearchResult {
        val engineId = engineIdMap[engineEnum] ?: throw AmiException.of(
            AmiErrorCode.AMI_S001_001,
            "`engineId` not found; engineEnum: $engineEnum"
        )

        val response = googleSearchClient.search(query, engineId)
        val result = SearchResult().also { searchResult ->
            searchResult.provider = SearchProviderEnum.GOOGLE
            searchResult.engine = engineEnum
            searchResult.query = query
            searchResult.correctedQuery = response.spelling?.correctedQuery
            searchResult.results = response.items.map { item ->
                SearchResultEntry().also { searchResultEntry ->
                    searchResultEntry.title = item.title
                    searchResultEntry.url = item.link
                    searchResultEntry.snippet = item.snippet
                    searchResultEntry.site = item.displayLink
                }
            }
        }

        return result
    }

    override suspend fun searchIntegrated(
        query: String,
        mainEngineEnums: List<SearchEngineEnum>,
        restEngineEnum: SearchEngineEnum?,
    ): Map<SearchEngineEnum, SearchResult> {
        if (mainEngineEnums.isEmpty()) {
            throw AmiException.of(
                AmiErrorCode.AMI_S001_001,
                "`mainEngineEnums` is empty"
            )
        }

        // Search all the engines and dedup.
        // All results from the main engines are preserved.
        // The results from the rest engine are deduped and unique.
        val urls = mutableSetOf<String>();
        val resultMap = mutableMapOf<SearchEngineEnum, SearchResult>()
        mainEngineEnums.forEach { engineEnum ->
            val searchResult = search(query, engineEnum)
            searchResult.results.forEach {
                urls.add(it.url)
            }
            resultMap[engineEnum] = searchResult
        }

        if (restEngineEnum != null) {
            val searchResult = search(query, restEngineEnum)
            val deduppedUrls = searchResult.results.filter {
                !urls.contains(it.url)
            }.toList()
            searchResult.results = deduppedUrls
            resultMap[restEngineEnum] = searchResult
        }

        return resultMap
    }

}