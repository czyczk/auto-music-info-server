package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class Spelling {

    lateinit var correctedQuery: String

    var htmlCorrectedQuery: String = ""

}