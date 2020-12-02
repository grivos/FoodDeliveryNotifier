package com.invariantlabs.fooddeliverynotifier.presentation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantScreen
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsIntent
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsViewState
import com.invariantlabs.fooddeliverynotifier.restaurants.R
import com.invariantlabs.fooddeliverynotifier.restaurants.databinding.FragmentMonitoredRestaurantsBinding
import com.invariantlabs.presentation.mvi.MviViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.qualifier.named

internal class MonitoredRestaurantsFragment :
    BaseRestaurantsFragment<FragmentMonitoredRestaurantsBinding>(
        R.layout.fragment_monitored_restaurants,
        FragmentMonitoredRestaurantsBinding::bind
    ),
    RestaurantsAdapter.Callback {

    private val viewModel: RestaurantsViewModel by stateViewModel(named(RestaurantScreen.Monitored))
    override val model: MviViewModel<RestaurantsIntent, RestaurantsAction, RestaurantsViewState>
        get() = viewModel

    override val recyclerView: RecyclerView
        get() = binding.monitoredRecycler
    override val refreshView: SwipeRefreshLayout
        get() = binding.monitoredRefresh
    override val errorView: View
        get() = binding.monitoredError
    override val emptyView: View
        get() = binding.monitoredEmpty
    override val tryAgainView: View
        get() = binding.tryAgain
}
