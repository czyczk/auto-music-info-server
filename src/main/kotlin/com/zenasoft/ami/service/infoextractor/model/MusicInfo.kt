package com.zenasoft.ami.service.infoextractor.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class MusicInfo {

    /**
     * From which URL the info is extracted.
     */
    lateinit var url: String

    /**
     * The query. Usually contains the artist and the title.
     */
    lateinit var query: String

    lateinit var artist: String

    lateinit var title: String

    var album: String? = null

    /**
     * The release date.
     */
    var date: LocalDate? = null

    var genre: String? = null

    var trackNo: Int? = null

    var composer: String? = null

    var lyricist: String? = null

    var arranger: String? = null

    var confidence: String? = null

}