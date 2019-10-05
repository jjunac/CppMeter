package com.github.jjunac.cppmeter.displayers

import com.github.jjunac.cppmeter.events.DisplayEvent

interface Displayer {

    fun display(displayEvent: DisplayEvent): Any

}