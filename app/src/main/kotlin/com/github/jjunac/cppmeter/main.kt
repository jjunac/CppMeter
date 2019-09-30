package com.github.jjunac.cppmeter

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.freemarker.FreeMarker
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {
    val core = Core("C:/Users/jerem/Xshared/MQLite/src")
    core.analyse()

    val server = embeddedServer(Netty, 8080) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this.javaClass.classLoader, "templates")
        }
        routing {
            get("/{plugin?}") {
                call.respond(call.parameters["plugin"]?.let { it1 -> core.displayPlugin(it1) } ?: core.displayOverview())
            }
            static("/static") {
                resources("static")
            }
        }
    }
    server.start(wait = true)
}

