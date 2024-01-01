package com.zenasoft.ami.controller

import com.zenasoft.ami.common.exception.AmiErrorCode
import com.zenasoft.ami.common.exception.AmiException
import com.zenasoft.ami.controller.model.AmiControllerView
import com.zenasoft.ami.controller.model.InfoExtractorRequest
import com.zenasoft.ami.service.infoextractor.IInfoExtractor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class InfoExtractorController : KoinComponent {

    private val infoExtractor: IInfoExtractor by inject(named("infoExtractorSelector"))

    fun getRoutes(routing: Routing) {
        routing.route("/api/v1/info-extractor") {
            post("/") {
                val (params, isOk) = checkAndExtractParams(call)
                if (!isOk) {
                    return@post
                }

                val musicInfo = infoExtractor.extractMusicInfo(params.url, params.query)
                call.respond(AmiControllerView.ofOk(musicInfo))
            }
        }
    }

    private suspend fun checkAndExtractParams(call: ApplicationCall): Pair<InfoExtractorRequest, Boolean> {
        try {
            val request = call.receive<InfoExtractorRequest>()
            return Pair(request, true)
        } catch (e: BadRequestException) {
            call.response.status(HttpStatusCode.BadRequest)
            call.respond(AmiControllerView.ofError<Void>(AmiException.of(AmiErrorCode.AMI_C001_001, e.message ?: "")))
            return Pair(InfoExtractorRequest(), false)
        }
    }

}