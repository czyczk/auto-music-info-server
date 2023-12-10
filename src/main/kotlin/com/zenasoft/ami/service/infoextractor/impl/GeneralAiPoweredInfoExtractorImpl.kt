package com.zenasoft.ami.service.infoextractor.impl

import com.zenasoft.ami.common.AmiContext
import com.zenasoft.ami.service.infoextractor.AbstractAiPoweredInfoExtractor
import com.zenasoft.ami.service.infoextractor.model.MusicInfo
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeneralAiPoweredInfoExtractorImpl : KoinComponent, AbstractAiPoweredInfoExtractor() {

    private val logger = KotlinLogging.logger { }

    private val ctx: AmiContext by inject()

    private val jsonKSerializer: Json by inject()

    private val okHttpClient: OkHttpClient by inject()

    override fun extractMusicInfo(url: String, query: String): MusicInfo {
        TODO("Not yet implemented")
    }

}