package com.zenasoft.ami.controller

import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import com.zenasoft.ami.controller.model.AmiControllerView
import com.zenasoft.ami.controller.model.IntegratedSearchRequest
import com.zenasoft.ami.service.googlesearch.IGoogleSearchService
import com.zenasoft.ami.service.googlesearch.model.type.SearchEngineEnum
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchController : KoinComponent {

    private val googleSearchService: IGoogleSearchService by inject()

    fun getRoutes(routing: Routing) {
        routing.route("/api/v1/search") {
            post("/integrated") {
                // Query all search engines and dedup.
                // Get the query string from the request.
                val (query, isOk) = checkAndExtractQuery(call)
                if (!isOk) {
                    return@post
                }

                val view = AmiControllerView.wrapSuspend {
                    val searchResultMap = googleSearchService.searchIntegrated(
                        query,
                        // all the engines except for ENTIRE
                        SearchEngineEnum.entries.filter { it != SearchEngineEnum.ENTIRE },
                        SearchEngineEnum.ENTIRE,
                    )
                    searchResultMap
                }
                call.respond(view)
            }
            post("/google/{engineId}") {
                // Get the engine ID from the path.
                val engineId = call.parameters["engineId"]!!.lowercase()
                val engineEnum = SearchEngineEnum.entries.find { it.engineId == engineId }
                if (engineEnum == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AmiControllerView.ofError<Void>(
                            AmiException.of(
                                AmiErrorCode.AMI_C001_002,
                                "The specified engine does not exist.",
                            ),
                        ),
                    )
                    return@post
                }

                // Get the query string from the request.
                val query = call.request.queryParameters["query"] ?: ""
                if (query.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AmiControllerView.ofError<Void>(
                            AmiException.of(
                                AmiErrorCode.AMI_C001_001,
                                "Please specify a query string.",
                            ),
                        ),
                    )
                    return@post
                }

                val view = AmiControllerView.wrapSuspend {
                    val searchResult = googleSearchService.search(query, engineEnum)
                    searchResult
                }
                call.respond(view)
            }
        }
    }

    private suspend fun checkAndExtractQuery(call: ApplicationCall): Pair<String, Boolean> {
        val request = call.receive<IntegratedSearchRequest>()
        if (request.query.isBlank()) {
            call.response.status(HttpStatusCode.BadRequest)
            call.respond(
                HttpStatusCode.BadRequest,
                AmiControllerView.ofError<Void>(
                    AmiException.of(
                        AmiErrorCode.AMI_C001_001,
                        "Please specify a query string.",
                    ),
                ),
            )
            return Pair("", false)
        }
        return Pair(request.query, true)
    }

}