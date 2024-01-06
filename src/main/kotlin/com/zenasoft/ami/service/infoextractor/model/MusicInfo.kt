package com.zenasoft.ami.service.infoextractor.model

import kotlinx.serialization.Serializable

@Serializable
open class MusicInfo(
    var artist: List<String>,
    var title: String,
    var album: String? = null,

    /**
     * The release date.
     */
    var date: String? = null,

    var genre: String? = null,
    var trackNo: Int? = null,
    var composer: List<String>? = null,
    var lyricist: List<String>? = null,
    var arranger: List<String>? = null,
    var confidence: String? = null,
    var lowConfidenceReason: String? = null,
) {

    companion object {
        // Make extensions possible.
    }

}