package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import io.ktor.freemarker.FreeMarkerContent

abstract class Plugin(val name: String) {

    abstract fun preAnalyse(e: PreAnalyseEvent)
    abstract fun analyse(e: AnalyseEvent)
    abstract fun postAnalyse(e: PostAnalyseEvent)

    abstract fun display(e: DisplayEvent): FreeMarkerContent

}
