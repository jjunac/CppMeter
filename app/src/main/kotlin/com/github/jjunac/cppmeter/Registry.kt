package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.analysers.Analyser
import com.github.jjunac.cppmeter.annotations.RegisterAnalyser
import com.github.jjunac.cppmeter.annotations.RegisterView
import com.github.jjunac.cppmeter.views.View
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner

object Registry {

    val analysers = mutableSetOf<Class<out Analyser>>()
    val viewMap = mutableMapOf<String, Class<out View>>()

    private val logger = KotlinLogging.logger {}
    private val reflections = Reflections("com.github.jjunac.cppmeter", SubTypesScanner(), TypeAnnotationsScanner())

    private val registeringAnnotation: Map<Class<*>, Class<out Annotation>> = mapOf(
        Analyser::class.java to RegisterAnalyser::class.java,
        View::class.java to RegisterView::class.java
    )

    fun discover() {
        forEachRegistered(Analyser::class.java) {
            analysers += it
        }
        logger.info { "${analysers.size} Analysers registered" }
        forEachRegistered(View::class.java) {
            viewMap[it.getAnnotation(RegisterView::class.java).path] = it
        }
        logger.info { "${viewMap.size} Views registered" }
    }

    private fun <T : Class<*>> forEachRegistered(clazz: T, action: (T) -> Unit) {
        reflections.getTypesAnnotatedWith(registeringAnnotation[clazz]).forEach {
            require (clazz.isAssignableFrom(it)) { "${it.simpleName} doesn't extend ${clazz.simpleName}" }
            @Suppress("UNCHECKED_CAST")
            action(it as T)
        }
    }
    
}
