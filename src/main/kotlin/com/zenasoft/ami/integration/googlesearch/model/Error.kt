package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable

@Serializable
class Error {

    val code: Int = 0

    lateinit var message: String

    lateinit var errors: List<ErrorEntry>

    lateinit var status: String

}