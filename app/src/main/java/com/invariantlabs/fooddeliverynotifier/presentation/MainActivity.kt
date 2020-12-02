package com.invariantlabs.fooddeliverynotifier.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.invariantlabs.fooddeliverynotifier.R
import com.invariantlabs.fooddeliverynotifier.databinding.ActivityMainBinding
import com.invariantlabs.presentation.base.BaseActivity
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val navigator: Navigator by inject()

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        navigator.bind(navController)
    }

    override fun onPause() {
        navigator.unbind()
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private val navController: NavController
        get() {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            return navHostFragment.navController
        }
}
