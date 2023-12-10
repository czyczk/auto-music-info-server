package com.zenasoft.ami.integration.perplexityapi.model.type

import kotlinx.serialization.Serializable

@Serializable
enum class RoleEnum(val apiCode: String) {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    ;

    companion object {
        fun fromApiCode(apiCode: String): RoleEnum {
            return entries.firstOrNull { it.apiCode == apiCode }
                ?: throw IllegalArgumentException("No enum constant $apiCode")
        }
    }

}