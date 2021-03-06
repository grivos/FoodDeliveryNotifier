package com.invariantlabs.presentation.extensions

import io.reactivex.Observable

fun <T : Any, U : Any> Observable<T>.notOfType(clazz: Class<U>): Observable<T> =
    filter { !clazz.isInstance(it) }
