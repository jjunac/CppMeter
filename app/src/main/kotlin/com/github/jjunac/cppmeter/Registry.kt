package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.analysers.Analyser
import com.github.jjunac.cppmeter.annotations.RegisterAnalyser
import com.github.jjunac.cppmeter.annotations.RegisterView
import com.github.jjunac.cppmeter.views.View
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner

open class Registry<K, V>(private val internalMap: MutableMap<K, V>) : Map<K, V> by internalMap {

    companion object {

        val analysers = ClassRegistry<Analyser>(mutableMapOf())
        val views = Registry<String, View>(mutableMapOf())

        private val logger = KotlinLogging.logger {}
        private val reflections = Reflections("com.github.jjunac.cppmeter", SubTypesScanner(), TypeAnnotationsScanner())

        private val registeringAnnotation: Map<Class<*>, Class<out Annotation>> = mapOf(
            Analyser::class.java to RegisterAnalyser::class.java,
            View::class.java to RegisterView::class.java
        )

        fun register() {
            forEachRegistered(Analyser::class.java)
                { analysers.register(it, it.getConstructor().newInstance()) }
            logger.info { "${analysers.size} Analysers registered" }
            forEachRegistered(View::class.java)
                { views.register(it.getAnnotation(RegisterView::class.java).path, it.getConstructor().newInstance()) }
            logger.info { "${views.size} Views registered" }
        }

        private fun <T : Class<*>> forEachRegistered(clazz: T, action: (T) -> Unit) {
            reflections.getTypesAnnotatedWith(registeringAnnotation[clazz]).forEach {
                if (!clazz.isAssignableFrom(it))
                    error("${it.simpleName} doesn't extend ${clazz.simpleName}")
                @Suppress("UNCHECKED_CAST")
                action(it as T)
            }
        }
    }

    fun register(key: K, value: V) {
        internalMap[key] = value
    }

}

class ClassRegistry<T>(internalMap: MutableMap<Class<out T>, T>) : Registry<Class<out T>, T>(internalMap) {
    inline fun <reified U: T> getInstance(key: Class<U>): U {
        require(super.containsKey(key)) { "Class ${key.simpleName} not registered" }
        return super.get(key) as U
    }
}
