package com.pegas.origami.paper.folding.art.app

import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.inter_welcome_back
import com.pegas.origami.paper.folding.art.ads.native_welcome_back

enum class ResumeAdsEntryMode {
    OPEN_RESUME,
    WELCOME,
    NONE,
}

object ResumeAdsEntryRule {
    fun currentMode(): ResumeAdsEntryMode {
        if (!AdRemoteConfig.isInitialized()) return ResumeAdsEntryMode.NONE

        val canUseWelcome =
            AdRemoteConfig.native_welcome_back.isEnable && AdRemoteConfig.inter_welcome_back.isEnable
        if (canUseWelcome) return ResumeAdsEntryMode.WELCOME

        val canUseOpenResume = false
        return if (canUseOpenResume) ResumeAdsEntryMode.OPEN_RESUME else ResumeAdsEntryMode.NONE
    }

    fun shouldEnableOpenResume(): Boolean = currentMode() == ResumeAdsEntryMode.OPEN_RESUME

    fun shouldShowWelcomeOnResume(): Boolean = currentMode() == ResumeAdsEntryMode.WELCOME
}
