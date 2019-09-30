package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent

abstract class Plugin(val core: Core, val name: String) {

    abstract fun preAnalyse(e: PreAnalyseEvent)
    abstract fun analyse(e: AnalyseEvent)
    abstract fun postAnalyse(e: PostAnalyseEvent)

    abstract fun displayPage(e: DisplayEvent): PageDisplayer

    open fun getDisplayableName(): String {
        return name.capitalize()
    }

    protected fun displayPageFromTemplate(dataModel: Map<String, Any>): PageDisplayer {
        return PageDisplayer(
            "plugins/$name.ftl",
            dataModel
        )
    }

}
