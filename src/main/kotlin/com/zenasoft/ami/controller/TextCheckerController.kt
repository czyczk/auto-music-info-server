package com.zenasoft.ami.controller

import com.zenasoft.ami.common.type.TextLanguageEnum
import com.zenasoft.ami.controller.model.AmiControllerView
import com.zenasoft.ami.service.textchecker.ITextCheckerService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TextCheckerController : KoinComponent {

    private val textCheckerService: ITextCheckerService by inject()

    fun getRoutes(routing: Routing) {
        routing.route("/api/v1/text-checker") {
            get("validity") {
                // Check text validity against a specified text language.
                // Get the query string from the request.
                val (request, isOk) = checkAndExtractQuery(call)
                if (!isOk) {
                    return@get
                }

                val view = AmiControllerView.wrap {
                    val textValidityResult = textCheckerService.checkTextValidity(
                        request!!.text,
                        request.textLanguage,
                    )
                    textValidityResult
                }
                call.respond(view)
            }
        }
    }

    private suspend fun checkAndExtractQuery(call: ApplicationCall): Pair<TextCheckerRequest?, Boolean> {
        val text = call.request.queryParameters["text"]
        if (text.isNullOrBlank()) {
            call.response.status(HttpStatusCode.BadRequest)
            call.respondText("Please specify a text.")
            return Pair(null, false)
        }

        val textLanguageStr = call.request.queryParameters["textLanguage"]
        var textLanguage: TextLanguageEnum? = null
        if (!textLanguageStr.isNullOrBlank()) {
            try {
                textLanguage = TextLanguageEnum.valueOf(textLanguageStr)
            } catch (e: IllegalArgumentException) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respondText("Invalid text language of $textLanguageStr.")
                return Pair(null, false)
            }
        }

        return Pair(TextCheckerRequest(text, textLanguage), true)
    }

}

private class TextCheckerRequest(val text: String, val textLanguage: TextLanguageEnum?)