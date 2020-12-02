package com.invariantlabs.presentation.base

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity<VB : ViewBinding>(
    bindingInflater: (LayoutInflater) -> VB
) : AppCompatActivity() {

    protected val binding by viewBinding(bindingInflater)

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
