package com.zenasoft.ami.service.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class SearchResultEntry {

    lateinit var title: String

    lateinit var url: String

    var snippet: String = ""

    /**
     * From [com.zenasoft.ami.integration.googlesearch.model.ItemEntry.displayLink].
     * E.g. "en.wikipedia.org".
     */
    lateinit var site: String

}