package com.github.jjunac.cppmeter.views

import com.github.jjunac.cppmeter.Registry
import com.github.jjunac.cppmeter.displayers.Displayer
import com.github.jjunac.cppmeter.displayers.PageDisplayer

abstract class PageView(val name: String) : View {

    override val displayableName: String
        get() = name.capitalize()

    override var displayer: Displayer? = null

    abstract fun buildDataModel(): Map<String, Any>

    override fun buildDisplayer() {
        displayer = PageDisplayer("plugins/$name.ftl", buildDataModel())
    }

}