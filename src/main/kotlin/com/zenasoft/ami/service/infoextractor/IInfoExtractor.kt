package com.zenasoft.ami.service.infoextractor

import com.zenasoft.ami.service.infoextractor.model.MusicInfoWithRequest

interface IInfoExtractor {

    suspend fun extractMusicInfo(url: String, query: String): MusicInfoWithRequest

}