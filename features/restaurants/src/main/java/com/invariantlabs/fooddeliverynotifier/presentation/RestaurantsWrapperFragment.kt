package com.invariantlabs.fooddeliverynotifier.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mauker.materialsearchview.MaterialSearchView
import com.google.android.material.tabs.TabLayoutMediator
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperAction
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperIntent
import com.invariantlabs.fooddeliverynotifier.domain.model.RestaurantsWrapperViewState
import com.invariantlabs.fooddeliverynotifier.navigation.RestaurantsNavigation
import com.invariantlabs.fooddeliverynotifier.restaurants.R
import com.invariantlabs.fooddeliverynotifier.restaurants.databinding.FragmentRestaurantsWrapperBinding
import com.invariantlabs.presentation.mvi.BaseMviFragment
import com.invariantlabs.presentation.mvi.MviViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class RestaurantsWrapperFragment :
    BaseMviFragment<FragmentRestaurantsWrapperBinding, RestaurantsWrapperIntent, RestaurantsWrapperAction, RestaurantsWrapperViewState>(
        R.layout.fragment_restaurants_wrapper,
        FragmentRestaurantsWrapperBinding::bind
    ) {

    private val intentsRelay = PublishRelay.create<RestaurantsWrapperIntent>()
    private val viewModel: RestaurantsWrapperViewModel by stateViewModel()
    override val model: MviViewModel<RestaurantsWrapperIntent, RestaurantsWrapperAction, RestaurantsWrapperViewState>
        get() = viewModel
    private val restaurantsNavigation: RestaurantsNavigation by inject()

    override val intents: Observable<RestaurantsWrapperIntent>
        get() =
            Observable.merge(
                listOf(
                    intentsRelay,
                )
            )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSearch()
        setupPager()
    }

    private fun setupPager() {
        binding.pager.offscreenPageLimit = 1
        binding.pager.adapter = PagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.title_favorites)
                1 -> getString(R.string.title_monitored)
                else -> throw IllegalStateException("Unknown pager position: $position")
            }
        }.attach()
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                intentsRelay.accept(RestaurantsWrapperIntent.EnterSearchQueryIntent(query))
                return false
            }
        })
        binding.searchView.setSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                intentsRelay.accept(RestaurantsWrapperIntent.CloseSearchIntent)
            }

            override fun onSearchViewOpened() {
                // no-op
            }
        })
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.search_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    intentsRelay.accept(RestaurantsWrapperIntent.ClickSearchIntent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.searchView.isOpen) {
                        binding.searchView.closeSearch()
                    } else {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            }
        )
    }

    override fun render(state: RestaurantsWrapperViewState) {
        binding.apply {
            if (state.showSearch) {
                searchView.openSearch()
            } else {
                searchView.closeSearch()
            }
            state.goToSearchResults?.consume { searchQuery ->
                restaurantsNavigation.navigateToSearch(searchQuery!!.query)
            }
        }
    }

    private class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FavoriteRestaurantsFragment()
                1 -> MonitoredRestaurantsFragment()
                else -> throw IllegalStateException("Unknown pager position: $position")
            }
        }
    }
}
