package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class GoogleSearchOkResponse {

    var kind: String = ""

    lateinit var url: Url

    lateinit var queries: Queries

    // The context can be null if the search result is empty.
    var context: Context? = null

    var spelling: Spelling? = null

    var items: List<ItemEntry> = listOf()

}