package com.zenasoft.ami.service.infoextractor

import com.zenasoft.ami.integration.perplexityapi.model.Message

abstract class AbstractAiPoweredInfoExtractor : IInfoExtractor {

    abstract fun buildInitialMessages(url: String, query: String): List<Message>

}