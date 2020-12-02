package com.invariantlabs.fooddeliverynotifier.presentation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantScreen
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsIntent
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsViewState
import com.invariantlabs.fooddeliverynotifier.restaurants.R
import com.invariantlabs.fooddeliverynotifier.restaurants.databinding.FragmentFavoriteRestaurantsBinding
import com.invariantlabs.presentation.mvi.MviViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.qualifier.named

internal class FavoriteRestaurantsFragment :
    BaseRestaurantsFragment<FragmentFavoriteRestaurantsBinding>(
        R.layout.fragment_favorite_restaurants,
        FragmentFavoriteRestaurantsBinding::bind
    ),
    RestaurantsAdapter.Callback {

    private val viewModel: RestaurantsViewModel by stateViewModel(named(RestaurantScreen.Favorites))
    override val model: MviViewModel<RestaurantsIntent, RestaurantsAction, RestaurantsViewState>
        get() = viewModel

    override val recyclerView: RecyclerView
        get() = binding.favoritesRecycler
    override val refreshView: SwipeRefreshLayout
        get() = binding.favoritesRefresh
    override val errorView: View
        get() = binding.favoritesError
    override val emptyView: View
        get() = binding.favoritesEmpty
    override val tryAgainView: View
        get() = binding.tryAgain
}
