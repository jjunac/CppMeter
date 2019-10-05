package com.github.jjunac.cppmeter.views

import com.github.jjunac.cppmeter.displayers.Displayer

interface View {

    val displayableName: String
    var displayer: Displayer?


    fun buildDisplayer()

}