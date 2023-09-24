package com.zenasoft.ami.controller

import com.zenasoft.ami.controller.model.AmiControllerView
import com.zenasoft.ami.service.googlesearch.IGoogleSearchService
import com.zenasoft.ami.service.googlesearch.type.SearchEngineEnum
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GoogleSearchController : KoinComponent {

    private val googleSearchService: IGoogleSearchService by inject()

    fun getRoutes(routing: Routing) {
        routing.route("/api/v1/google") {
            get("/{engineId}") {
                // Get the engine ID from the path.
                val engineId = call.parameters["engineId"]!!.lowercase()
                val engineEnum = SearchEngineEnum.entries.find { it.engineId == engineId }
                if (engineEnum == null) {
                    call.response.status(HttpStatusCode.NotFound)
                    return@get
                }

                // Get the query string from the request.
                val query = call.request.queryParameters["query"] ?: ""
                if (query.isBlank()) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respondText("Please specify a query string.")
                    return@get
                }

                val view = AmiControllerView.wrapSuspend {
                    val searchResult = googleSearchService.search(query, engineEnum)
                    searchResult
                }
                call.respond(view)
            }
        }
    }

}