package com.invariantlabs.fooddeliverynotifier.inject

import androidx.lifecycle.SavedStateHandle
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantScreen
import com.invariantlabs.fooddeliverynotifier.presentation.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.qualifier.named
import org.koin.dsl.module

object RestaurantsModules {
    fun injectFeature() = loadFeature
}

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            viewModelModule,
        )
    )
}

val viewModelModule: Module = module {
    viewModel(named(RestaurantScreen.Search)) { params: DefinitionParameters ->
        val handle: SavedStateHandle = params[0]
        val query: String = params[1]
        val processor =
            SearchProcessor(query = query, woltService = get(), locationStore = get())
        RestaurantsViewModel(handle = handle, processor = processor)
    }
    viewModel(named(RestaurantScreen.Favorites)) { params: DefinitionParameters ->
        val handle: SavedStateHandle = params[0]
        val processor = FavoritesProcessor(woltService = get(), locationStore = get())
        RestaurantsViewModel(handle = handle, processor = processor)
    }
    viewModel(named(RestaurantScreen.Monitored)) { params: DefinitionParameters ->
        val handle: SavedStateHandle = params[0]
        val processor = MonitoredProcessor(woltService = get(), locationStore = get())
        RestaurantsViewModel(handle = handle, processor = processor)
    }
    viewModel { params: DefinitionParameters ->
        val handle: SavedStateHandle = params[0]
        val processor = RestaurantsWrapperProcessor()
        RestaurantsWrapperViewModel(handle = handle, processor = processor)
    }
}
