package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class ItemEntry {

    var kind: String = ""

    lateinit var title: String

    var htmlTitle: String = ""

    lateinit var link: String

    lateinit var displayLink: String

    var snippet: String = ""

    var cacheId: String = ""

    var formattedUrl: String = ""

    var htmlFormattedUrl: String = ""

}