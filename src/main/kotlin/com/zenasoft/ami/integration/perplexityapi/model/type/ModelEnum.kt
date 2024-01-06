package com.zenasoft.ami.integration.perplexityapi.model.type

enum class ModelEnum(val apiCode: String) {

    /*
     * Include only online models.
     */

    PPLX_7B_ONLINE("pplx-7b-online"),
    PPLX_70B_ONLINE("pplx-70b-online"),
    ;

    companion object {
        fun fromApiCode(apiCode: String): ModelEnum {
            return entries.firstOrNull { it.apiCode == apiCode }
                ?: throw IllegalArgumentException("No enum constant $apiCode")
        }
    }

}