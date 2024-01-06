package com.zenasoft.ami.integration.perplexityapi.model

data class ResponseChoice(
    var index: Int,
    var finishReason: String,
    var message: Message,
    var delta: Message,
) {
    companion object {
        // Make extensions possible.
    }
}
