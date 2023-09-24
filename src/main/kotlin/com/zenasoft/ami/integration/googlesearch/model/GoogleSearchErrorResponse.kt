package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class GoogleSearchErrorResponse {

    lateinit var error: Error

}