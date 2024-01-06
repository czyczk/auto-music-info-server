package com.zenasoft.ami.integration.perplexityapi.model

data class ResponseUsage(
    var promptTokens: Int,
    var completionTokens: Int,
    var totalTokens: Int,
) {
    companion object {
        // Make extensions possible.
    }
}
