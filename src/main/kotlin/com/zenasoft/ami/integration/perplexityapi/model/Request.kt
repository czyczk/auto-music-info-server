package com.zenasoft.ami.integration.perplexityapi.model

import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum

data class Request(
    var model: ModelEnum,
    var messages: List<Message>,
) {
    companion object {
        // Make extensions possible.
    }
}