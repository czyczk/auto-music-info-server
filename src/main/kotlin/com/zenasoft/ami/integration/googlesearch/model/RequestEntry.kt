package com.zenasoft.ami.integration.googlesearch.model

import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
class RequestEntry {

    var title: String = ""

    @Serializable(with = BigIntegerStringSerializer::class)
    var totalResults: BigInteger = BigInteger.ZERO

    var searchTerms: String = ""

    var count: Int = 0

    var startIndex: Int = 0

    var inputEncoding: String = ""

    var outputEncoding: String = ""

    var safe: String = ""

    lateinit var cx: String

}
