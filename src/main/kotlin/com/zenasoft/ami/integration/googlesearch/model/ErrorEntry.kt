package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class ErrorEntry {

    lateinit var message: String

    lateinit var domain: String

    lateinit var reason: String

}