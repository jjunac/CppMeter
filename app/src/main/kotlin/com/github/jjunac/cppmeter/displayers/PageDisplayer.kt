package com.github.jjunac.cppmeter.displayers

import com.github.jjunac.cppmeter.Registry
import com.github.jjunac.cppmeter.events.DisplayEvent
import io.ktor.freemarker.FreeMarkerContent
import mu.KotlinLogging

class PageDisplayer(val template: String, val dataModel: Map<String, Any>) : Displayer {

    val logger = KotlinLogging.logger{}

    override fun display(displayEvent: DisplayEvent): Any {
        val finalDataModel = dataModel.toMutableMap()
        finalDataModel["plugins"] = Registry.views.mapValues { it.value.displayableName }
        logger.debug { finalDataModel }
        return FreeMarkerContent(template, finalDataModel)
    }

}
