package com.github.jjunac.cppmeter.analysers

import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent

interface Analyser {
    fun preAnalyse(e: PreAnalyseEvent)
    fun analyse(e: AnalyseEvent)
    fun postAnalyse(e: PostAnalyseEvent)
}
