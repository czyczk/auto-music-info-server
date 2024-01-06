package com.zenasoft.ami.integration.perplexityapi.model

import com.zenasoft.ami.integration.perplexityapi.model.type.RoleEnum

data class Message(
    var role: RoleEnum,
    var content: String
) {
    companion object {
        // Make extensions possible.
    }
}
