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
    val core = Core("C:/Users/jerem/Xshared/MQLite/include")
    core.analyse()

    val server = embeddedServer(Netty, 8080) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this.javaClass.classLoader, "templates")
        }
        routing {
            get("/") {
                call.respond(core.display("dependencies"))
            }
            static("/static") {
                resources("static")
            }
        }
    }
    server.start(wait = true)
}

