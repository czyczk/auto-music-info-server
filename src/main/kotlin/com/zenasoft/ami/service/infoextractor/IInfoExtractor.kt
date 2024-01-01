package com.zenasoft.ami.service.infoextractor

import com.zenasoft.ami.service.infoextractor.model.MusicInfo

interface IInfoExtractor {

    suspend fun extractMusicInfo(url: String, query: String): MusicInfo

}