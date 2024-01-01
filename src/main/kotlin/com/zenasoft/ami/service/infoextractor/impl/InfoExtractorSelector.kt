package com.zenasoft.ami.service.infoextractor.impl

import com.zenasoft.ami.service.infoextractor.IInfoExtractor
import com.zenasoft.ami.service.infoextractor.model.MusicInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class InfoExtractorSelector : KoinComponent, IInfoExtractor {

    private val aiPoweredInfoExtractor: IInfoExtractor by inject(named("generalAiPoweredInfoExtractor"))

    override suspend fun extractMusicInfo(url: String, query: String): MusicInfo {
        // TODO: Use AI powered info extractor for all sites for now. Some sites are rule-based.
        return aiPoweredInfoExtractor.extractMusicInfo(url, query)
    }

}