package com.invariantlabs.fooddeliverynotifier.inject

import com.invariantlabs.fooddeliverynotifier.navigation.RestaurantsNavigation
import com.invariantlabs.fooddeliverynotifier.presentation.Navigator
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {
    fun injectFeature() = loadFeature
}

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            navigationModule,
        )
    )
}

val navigationModule: Module = module {
    single { Navigator() }
    single {
        val navigator: Navigator = get()
        @Suppress("USELESS_CAST")
        navigator as RestaurantsNavigation
    }
}
