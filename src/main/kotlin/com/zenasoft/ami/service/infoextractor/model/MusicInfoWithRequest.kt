package com.zenasoft.ami.service.infoextractor.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicInfoWithRequest(
    /**
     * From which URL the info is extracted.
     */
    var url: String,

    /**
     * The query. Usually contains the artist and the title.
     */
    var query: String,

    var musicInfo: MusicInfo,
) : MusicInfo(
    artist = musicInfo.artist,
    title = musicInfo.title,
    album = musicInfo.album,
    date = musicInfo.date,
    genre = musicInfo.genre,
    trackNo = musicInfo.trackNo,
    composer = musicInfo.composer,
    lyricist = musicInfo.lyricist,
    arranger = musicInfo.arranger,
    confidence = musicInfo.confidence,
    lowConfidenceReason = musicInfo.lowConfidenceReason,
) {

    companion object {
        // Make extensions possible.
    }

}
