package com.pegas.origami.paper.folding.art.app

import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.inter_welcome
import com.pegas.origami.paper.folding.art.ads.native_welcome
import com.pegas.origami.paper.folding.art.ads.open_resume

enum class ResumeAdsEntryMode {
    OPEN_RESUME,
    WELCOME,
    NONE,
}

object ResumeAdsEntryRule {
    fun currentMode(): ResumeAdsEntryMode {
        if (!AdRemoteConfig.isInitialized()) return ResumeAdsEntryMode.NONE

        val canUseWelcome =
            AdRemoteConfig.native_welcome.isEnable && AdRemoteConfig.inter_welcome.isEnable
        if (canUseWelcome) return ResumeAdsEntryMode.WELCOME

        val canUseOpenResume = AdRemoteConfig.open_resume.isEnable
        return if (canUseOpenResume) ResumeAdsEntryMode.OPEN_RESUME else ResumeAdsEntryMode.NONE
    }

    fun shouldEnableOpenResume(): Boolean = currentMode() == ResumeAdsEntryMode.OPEN_RESUME

    fun shouldShowWelcomeOnResume(): Boolean = currentMode() == ResumeAdsEntryMode.WELCOME
}
