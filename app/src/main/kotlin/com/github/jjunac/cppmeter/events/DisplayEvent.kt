package com.github.jjunac.cppmeter.events

data class DisplayEvent(val activeProject: String? = null, val viewNameMap: Map<String, String> = mapOf())
