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
import io.ktor.locations.*

@KtorExperimentalLocationsAPI
fun Application.main() {

    install(Locations)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this.javaClass.classLoader, "templates")
    }

    val core = Core("C:/Users/jerem/Xshared/MQLite/src")
    core.analyseProject()


    routing {
        get("/{view?}") {
            call.respond(call.parameters["view"]?.let { it1 -> core.displayView(it1) } ?: core.displayOverview())
        }
        static("/static") {
            resources("static")
        }
    }
}

@KtorExperimentalLocationsAPI
fun main() {
    embeddedServer(Netty, 8080, module = Application::main).start()
}

