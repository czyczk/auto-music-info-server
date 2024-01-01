package com.zenasoft.ami.controller.model

import kotlinx.serialization.Serializable

@Serializable
class InfoExtractorRequest {

    lateinit var url: String

    lateinit var query: String

}