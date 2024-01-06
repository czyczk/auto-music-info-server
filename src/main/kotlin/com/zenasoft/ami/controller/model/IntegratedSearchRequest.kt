package com.zenasoft.ami.controller.model

import kotlinx.serialization.Serializable

@Serializable
class IntegratedSearchRequest {
    lateinit var query: String
}