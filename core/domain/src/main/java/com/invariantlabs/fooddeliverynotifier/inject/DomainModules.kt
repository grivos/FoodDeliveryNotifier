package com.invariantlabs.fooddeliverynotifier.inject

import com.invariantlabs.fooddeliverynotifier.data.repository.LocationStore
import com.invariantlabs.fooddeliverynotifier.domain.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object DomainModules {
    fun injectFeature() = loadFeature
}

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            databaseModule,
            repositoryModule
        )
    )
}

val databaseModule: Module = module {
    single {
        val driver = AndroidSqliteDriver(Database.Schema, androidContext(), DB_FILENAME)
        Database(driver)
    }
}

val repositoryModule: Module = module {
    single { LocationStore(context = androidContext()) }
}

private const val DB_FILENAME = "restaurant.db"
