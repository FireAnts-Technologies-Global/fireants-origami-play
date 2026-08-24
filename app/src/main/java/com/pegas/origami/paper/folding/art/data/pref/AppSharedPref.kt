package com.pegas.origami.paper.folding.art.data.pref

import android.content.SharedPreferences

interface AppSharedPref {

    val sharedPref: SharedPreferences

    val editor: SharedPreferences.Editor

    var languageCode: String

    var firstLanguage: Boolean

    var firstOnBoarding: Boolean

    var isConfirmConsent: Boolean

    var isUserGlobal: Boolean

    var isRate: Boolean

    var isMusicEnabled: Boolean

    var isSoundFxEnabled: Boolean
    var isRateShownInSession: Boolean
}
