package com.zenasoft.ami.controller

import com.zenasoft.ami.service.pagefetcher.IPageFetcherService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExpController : KoinComponent {

    private val httpClientService: IPageFetcherService by inject()

    fun getRoutes(routing: Routing) {
        routing.route("/api/v1/exp") {
            get("/ping") {
                call.respondText("pong")
            }
            get("/google") {
                val pageContent = httpClientService.getGoogleHomepage()
                call.respondText(pageContent, ContentType.parse("text/html"))
            }
        }
    }

}