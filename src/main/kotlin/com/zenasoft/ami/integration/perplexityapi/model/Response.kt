package com.zenasoft.ami.integration.perplexityapi.model

import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum

data class Response(
    var id: String,
    var model: ModelEnum,
    var `object`: String,
    var created: Long,
    var usage: ResponseUsage,
    var choices: List<ResponseChoice>,
) {

    companion object {
        // Make extensions possible.
    }

}