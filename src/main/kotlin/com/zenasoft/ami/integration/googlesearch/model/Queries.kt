package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class Queries {

    var request: List<RequestEntry> = listOf()

    var nextPage: List<RequestEntry> = listOf()


}
