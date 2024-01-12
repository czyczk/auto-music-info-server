package com.zenasoft.ami.service.infoextractor.model

import kotlinx.serialization.Serializable

@Serializable
open class MusicInfo(
    var artists: List<String>,
    var title: String,
    var album: String? = null,

    /**
     * The release date.
     */
    var date: String? = null,

    var genre: String? = null,
    var trackNo: Int? = null,
    var composers: List<String>? = null,
    var lyricists: List<String>? = null,
    var arrangers: List<String>? = null,
    var confidence: String? = null,
    var lowConfidenceReason: String? = null,
) {

    companion object {
        // Make extensions possible.
    }

}