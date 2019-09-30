package com.github.jjunac.cppmeter.displayers

import com.github.jjunac.cppmeter.Plugin
import io.ktor.freemarker.FreeMarkerContent

data class PageDisplayer(val template: String, val dataModel: Map<String, Any>) {

    fun toFreeMarkerContent(plugins: Collection<Plugin>): FreeMarkerContent {
        val finalDataModel = dataModel.toMutableMap()
        finalDataModel["plugins"] = plugins.associate { it.name to it.getDisplayableName() }
        return FreeMarkerContent(template, finalDataModel)
    }

}
