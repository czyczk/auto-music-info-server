package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class GoogleSearchOkResponse {

    var kind: String = ""

    lateinit var url: Url

    lateinit var queries: Queries

    lateinit var context: Context

    var spelling: Spelling? = null

    var items: List<ItemEntry> = listOf()

}