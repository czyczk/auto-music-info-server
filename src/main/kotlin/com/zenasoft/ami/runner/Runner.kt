package com.zenasoft.ami.runner

import com.zenasoft.ami.common.AmiContext
import com.zenasoft.ami.controller.ExpController
import com.zenasoft.ami.controller.GoogleSearchController
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val logger = KotlinLogging.logger { }

class Runner {
    companion object : KoinComponent {

        private val ctx: AmiContext by inject()

        private val expController: ExpController by inject()

        private val googleSearchController: GoogleSearchController by inject()

        fun run(args: Array<String>) {
            embeddedServer(
                CIO,
                port = ctx.appConfig.network.port,
            ) {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    expController.getRoutes(this)
                    googleSearchController.getRoutes(this)
                }
            }.start(wait = true)
        }

    }

}
