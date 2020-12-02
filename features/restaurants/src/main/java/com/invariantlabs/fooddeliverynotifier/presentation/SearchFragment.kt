package com.invariantlabs.fooddeliverynotifier.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.mauker.materialsearchview.MaterialSearchView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.invariantlabs.fooddeliverynotifier.domain.model.*
import com.invariantlabs.fooddeliverynotifier.restaurants.R
import com.invariantlabs.fooddeliverynotifier.restaurants.databinding.FragmentSearchBinding
import com.invariantlabs.presentation.mvi.MviViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.util.*

class SearchFragment :
    BaseRestaurantsFragment<FragmentSearchBinding>(
        R.layout.fragment_search,
        FragmentSearchBinding::bind
    ),
    RestaurantsAdapter.Callback {

    private val viewModel: RestaurantsViewModel by stateViewModel(named(RestaurantScreen.Search)) {
        parametersOf(
            arguments?.getString(KEY_QUERY)
                ?: throw IllegalStateException("No query data")
        )
    }
    override val model: MviViewModel<RestaurantsIntent, RestaurantsAction, RestaurantsViewState>
        get() = viewModel

    override val recyclerView: RecyclerView
        get() = binding.searchRecycler
    override val refreshView: SwipeRefreshLayout
        get() = binding.searchRefresh
    override val errorView: View
        get() = binding.searchError
    override val emptyView: View
        get() = binding.searchEmpty
    override val tryAgainView: View
        get() = binding.tryAgain

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSearch()
        binding.toolbar.title = "\"${arguments?.getString(KEY_QUERY)}\""
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.changeLocationWrapper.setOnClickListener {
            intentsRelay.accept(RestaurantsIntent.ChangeLocationIntent)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.search_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    intentsRelay.accept(RestaurantsIntent.ClickSearchIntent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                intentsRelay.accept(RestaurantsIntent.EnterSearchQueryIntent(query))
                return false
            }
        })
        binding.searchView.setSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                intentsRelay.accept(RestaurantsIntent.CloseSearchIntent)
            }

            override fun onSearchViewOpened() {
                // no-op
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(
                true
            ) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_LOCATION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val place = Autocomplete.getPlaceFromIntent(data)
                place.addressComponents?.asList()
                val latitude = place.latLng!!.latitude
                val longitude = place.latLng!!.longitude
                val address = place.address!!
                intentsRelay.accept(
                    RestaurantsIntent.SelectedLocationIntent(
                        StoredLocation(
                            address,
                            latitude,
                            longitude
                        )
                    )
                )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun render(state: RestaurantsViewState) {
        super.render(state)
        binding.selectedLocation.text = state.storedLocation?.address
        state.goToChangeLocation?.consume { currentLocation ->
            pickLocation(currentLocation)
        }
        if (state.showSearch) {
            binding.searchView.openSearch()
        } else {
            binding.searchView.closeSearch()
        }
    }

    private fun pickLocation(currentLocation: StoredLocation?) {
        if (!Places.isInitialized()) {
            Places.initialize(
                requireContext(),
                getString(R.string.maps_api_key),
                Locale.getDefault()
            )
        }
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            listOf(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
            )
        )
            .setTypeFilter(TypeFilter.ADDRESS)
            .setInitialQuery(currentLocation?.address)
            .build(requireActivity())
        startActivityForResult(intent, REQ_LOCATION)
    }

    companion object {

        private const val KEY_QUERY = "query"
        private const val REQ_LOCATION = 1

        fun createBundle(query: String) =
            Bundle().apply {
                putString(KEY_QUERY, query)
            }
    }
}
