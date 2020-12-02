# Food Delivery Notifier
<p align="center">
  <img src="https://i.ibb.co/9ttzmN2/ic-launcher-playstore.png" width="180" title="logo">
</p>
Food Delivery Notifier is an open source Android app. It allows you to monitor your favorite restaurants on Wolt, and get notified when they resume delivery service.
It is available on the Google Play Store.
<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.invariantlabs.fooddeliverynotifier">
  <img
    alt="Get it on Google Play"
    height="80"
    src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"/>
</a>
</p>

## Screenshots
<p align="center"><img src="https://i.ibb.co/n1BYVCX/screenshot-en-01.png" width="250"></p>

## Tech-stack

### Programing language
Food Delivery Notifier is written entirely in Kotlin.

### Architecture
-  A single-activity architecture, using the [Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started) to manage fragment operations.
- MVI (Model-View-Intent) pattern (the implementation is derived from [JsonPlaceholderApp](https://github.com/ditn/JsonPlaceholderApp)).
- Multi module architecture.

### Libraries and tools
- [AndroidX](https://developer.android.com/jetpack/androidx)
- [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
- [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Jetpack Navigation Component](https://developer.android.com/guide/navigation)
- [RxJava2](https://github.com/ReactiveX/RxJava)
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
- [Koin](https://insert-koin.io/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Coil](https://coil-kt.github.io/coil/)
- [Retrofit](https://square.github.io/retrofit/)
- [Moshi](https://github.com/square/moshi)

## License
[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl-3.0.en.html)

Food Delivery Notifier is a free software: You can use, study, share and improve it at your
will. Specifically you can redistribute and/or modify it under the terms of the
[GNU General Public License](https://www.gnu.org/licenses/gpl.html) as
published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.