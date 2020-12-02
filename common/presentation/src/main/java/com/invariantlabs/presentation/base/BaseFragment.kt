package com.invariantlabs.presentation.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(
    @LayoutRes layout: Int,
    viewBindingFactory: (View) -> VB
) : Fragment(layout) {
    protected val binding by viewBinding(viewBindingFactory)
}
