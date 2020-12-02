package com.invariantlabs.fooddeliverynotifier.inject

import org.koin.core.context.loadKoinModules

object MonitorModules {
    fun injectFeature() = loadFeature
}

private val loadFeature by lazy {
    loadKoinModules(
        listOf()
    )
}
