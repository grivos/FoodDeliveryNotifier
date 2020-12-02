package com.invariantlabs.fooddeliverynotifier.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.invariantlabs.fooddeliverynotifier.domain.model.Restaurant
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsIntent
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsViewState
import com.invariantlabs.fooddeliverynotifier.navigation.RestaurantsNavigation
import com.invariantlabs.fooddeliverynotifier.restaurants.R
import com.invariantlabs.presentation.mvi.BaseMviFragment
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.koin.android.ext.android.inject

abstract class BaseRestaurantsFragment<VB : ViewBinding>(
    @LayoutRes layout: Int,
    viewBindingFactory: (View) -> VB
) : BaseMviFragment<VB, RestaurantsIntent, RestaurantsAction, RestaurantsViewState>(
    layout,
    viewBindingFactory
),
    RestaurantsAdapter.Callback {

    protected val intentsRelay = PublishRelay.create<RestaurantsIntent>()
    abstract val recyclerView: RecyclerView
    abstract val refreshView: SwipeRefreshLayout
    abstract val errorView: View
    abstract val emptyView: View
    abstract val tryAgainView: View
    private val restaurantsNavigation: RestaurantsNavigation by inject()
    final override val intents: Observable<RestaurantsIntent>
        get() =
            Observable.merge(
                listOf(
                    intentsRelay,
                )
            )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        refreshView.setOnRefreshListener {
            intentsRelay.accept(RestaurantsIntent.PullToRefreshIntent)
        }
        tryAgainView.setOnClickListener {
            intentsRelay.accept(RestaurantsIntent.TryAgainIntent)
        }
    }

    private fun setupRecycler() {
        recyclerView.apply {
            layoutManager =
                GridLayoutManager(requireContext(), resources.getInteger(R.integer.columns))
            adapter = RestaurantsAdapter(this@BaseRestaurantsFragment)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    override fun render(state: RestaurantsViewState) {
        binding.apply {
            when (state.type) {
                RestaurantsViewState.Type.Empty -> {
                    emptyView.isVisible = true
                    recyclerView.isVisible = false
                    errorView.isVisible = false
                }
                RestaurantsViewState.Type.Items -> {
                    emptyView.isVisible = false
                    recyclerView.isVisible = true
                    errorView.isVisible = false
                }
                RestaurantsViewState.Type.Error -> {
                    emptyView.isVisible = false
                    recyclerView.isVisible = false
                    errorView.isVisible = true
                }
            }

            refreshView.isRefreshing = state.showLoading
            (recyclerView.adapter as RestaurantsAdapter).submitList(state.restaurants)
            state.goToSearchResults?.consume { searchQuery ->
                restaurantsNavigation.navigateToSearch(searchQuery!!.query)
            }
            state.openRestaurant?.consume { restaurant ->
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = restaurant!!.publicUrl.toUri()
                })
            }
        }
    }

    override fun onClickRestaurant(restaurant: Restaurant) {
        intentsRelay.accept(RestaurantsIntent.ClickIntent(restaurant))
    }

    override fun onClickFavorite(restaurant: Restaurant) {
        intentsRelay.accept(RestaurantsIntent.ClickFavoriteIntent(restaurant))
    }

    override fun onClickMonitor(restaurant: Restaurant) {
        intentsRelay.accept(RestaurantsIntent.ClickMonitorIntent(restaurant))
    }
}
