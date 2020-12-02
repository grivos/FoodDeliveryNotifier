package com.invariantlabs.fooddeliverynotifier.presentation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.invariantlabs.fooddeliverynotifier.R
import com.invariantlabs.fooddeliverynotifier.navigation.RestaurantsNavigation

class Navigator : RestaurantsNavigation {

    private var navController: NavController? = null

    override fun navigateToSearch(query: String) {
        navController?.navigate(
            R.id.action_search,
            SearchFragment.createBundle(query),
            getNavOptions().build()
        )
    }

    private fun getNavOptions(): NavOptions.Builder = NavOptions.Builder()
        .setEnterAnim(R.anim.default_enter_anim)
        .setExitAnim(R.anim.default_exit_anim)
        .setPopEnterAnim(R.anim.default_pop_enter_anim)
        .setPopExitAnim(R.anim.default_pop_exit_anim)

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}
