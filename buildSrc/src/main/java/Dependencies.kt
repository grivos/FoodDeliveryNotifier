@file:Suppress("unused", "SpellCheckingInspection")

object ApplicationId {
    const val id = "com.invariantlabs.fooddeliverynotifier"
}

object Modules {
    const val app = ":app"
    const val domain = ":core:domain"
    const val wolt = ":core:wolt"
    const val presentation = ":common:presentation"
    const val network = ":common:network"
    const val restaurants = ":features:restaurants"
    const val monitor = ":features:monitor"
}

object Releases {
    const val versionCode = 1
    const val versionName = "1.0.0"
}

object Versions {
    const val compileSdk = 29
    const val minSdk = 21
    const val targetSdk = 29
    const val gradle = "4.1.1"
    const val kotlin = "1.4.10"

    const val appcompat = "1.3.0-alpha02"
    const val design = "1.3.0-alpha03"
    const val cardView = "1.0.0"
    const val recyclerView = "1.2.0-alpha06"
    const val swipeRefreshLayout = "1.2.0-alpha01"
    const val constraintLayout = "2.0.4"
    const val navigation = "2.3.1"
    const val ktx = "1.5.0-alpha05"
    const val timber = "4.7.1"
    const val rxjava = "2.2.20"
    const val rxAndroid = "2.1.1"
    const val rxkotlin = "2.4.0"
    const val okhttp = "4.9.0"
    const val loggingInterceptor = "3.1.0"
    const val lifecycle = "2.2.0"
    const val leakCanary = "2.5"
    const val koin = "2.1.5"
    const val rxRelay = "2.1.1"
    const val rxBinding = "3.1.0"
    const val spanomatic = "1.1.0"
    const val viewPump = "2.0.3"
    const val sqlDelight = "1.4.4"
    const val coil = "1.0.0"
    const val googleServices = "4.3.4"
    const val retrofit = "2.9.0"
    const val moshi = "1.11.0"
    const val jodaTime = "2.10.7.2"
    const val materialSearchView = "1.3.0-rc01"
    const val kotlinxCoroutinesAndroid = "1.4.1"
    const val datastore = "1.0.0-alpha04"
    const val protoBuf = "4.0.0-rc-2"
    const val reactiveStreams = "1.1.1"
    const val googlePlaces = "2.4.0"
    const val ktlint = "0.39.0"
}

object Libraries {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxkotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    const val rxRelay = "com.jakewharton.rxrelay2:rxrelay:${Versions.rxRelay}"
    const val rxBinding = "com.jakewharton.rxbinding3:rxbinding:${Versions.rxBinding}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val loggingInterceptor =
        "com.github.ihsanbal:LoggingInterceptor:${Versions.loggingInterceptor}"
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val lifecycleCommonJava8 =
        "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val lifecycleViewModelKtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleLiveDataKtx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val lifecycleProcess = "androidx.lifecycle:lifecycle-process:${Versions.lifecycle}"
    const val leakCanaryAndroid =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val koinAndroid = "org.koin:koin-android:${Versions.koin}"
    const val koinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
    const val spanomatic = "com.grivos.spanomatic:spanomatic:${Versions.spanomatic}"
    const val viewPump = "io.github.inflationx:viewpump:${Versions.viewPump}"
    const val sqlDelight = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    const val sqlDelightRxJava = "com.squareup.sqldelight:rxjava2-extensions:${Versions.sqlDelight}"
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshiCodeGen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    const val retrofitMoshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val retrofitRxjava2Adapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val jodaTime = "net.danlew:android.joda:${Versions.jodaTime}"
    const val materialSearchView =
        "br.com.mauker.materialsearchview:materialsearchview:${Versions.materialSearchView}"
    const val kotlinxCoroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.kotlinxCoroutinesAndroid}"
    const val datastore = "androidx.datastore:datastore:${Versions.datastore}"
    const val protoBuf = "com.google.protobuf:protobuf-javalite:${Versions.protoBuf}"
    const val reactiveStreams = "android.arch.lifecycle:reactivestreams:${Versions.reactiveStreams}"
    const val googlePlaces = "com.google.android.libraries.places:places:${Versions.googlePlaces}"
}

object SupportLibraries {
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val design = "com.google.android.material:material:${Versions.design}"
    const val cardView = "androidx.cardview:cardview:${Versions.cardView}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val swipeRefreshLayout =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val navigationFragment = "androidx.navigation:navigation-fragment:${Versions.navigation}"
    const val navigationFragmentKtx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
}