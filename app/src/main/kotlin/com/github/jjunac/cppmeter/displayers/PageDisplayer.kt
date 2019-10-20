package com.github.jjunac.cppmeter.displayers

import com.github.jjunac.cppmeter.Core
import com.github.jjunac.cppmeter.Registry
import com.github.jjunac.cppmeter.daos.Project
import com.github.jjunac.cppmeter.events.DisplayEvent
import freemarker.template.TemplateMethodModel
import freemarker.template.TemplateMethodModelEx
import io.ktor.freemarker.FreeMarkerContent
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction

class PageDisplayer(val template: String, val dataModel: Map<String, Any> = mapOf()) : Displayer {

    val logger = KotlinLogging.logger{}

    override fun display(displayEvent: DisplayEvent): Any {
        val finalDataModel = dataModel.toMutableMap()

        // Variable
        finalDataModel["plugins"] = displayEvent.viewNameMap
        finalDataModel["version"] = Core.version
        finalDataModel["codename"] = Core.codename
        finalDataModel["projects"] = transaction { Project.all().map { it.name } }
        finalDataModel["activeProject"] = displayEvent.activeProject ?: ""

        // Methods
        finalDataModel["getAvatar"] = AvatarSrcMethod()

        logger.debug { finalDataModel }
        return FreeMarkerContent(template, finalDataModel)
    }

}

class AvatarSrcMethod : TemplateMethodModelEx {
    companion object {
        val themes = arrayOf("sugarsweets", "heatwave", "daisygarden", "seascape", "summerwarmth", "bythepool",
            "duskfalling", "frogideas", "berrypie")
    }

    override fun exec(args: MutableList<Any?>?): Any {
        val p = args!![0].toString()
        val theme = themes[(p.hashCode() % themes.size + themes.size) % themes.size]
        return "http://tinygraphs.com/labs/isogrids/hexa/${p}?theme=${theme}&numcolors=4"
    }

}