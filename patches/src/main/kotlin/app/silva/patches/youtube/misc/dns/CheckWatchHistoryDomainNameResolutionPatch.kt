package app.silva.patches.youtube.misc.dns

import app.silva.patches.shared.misc.dns.checkWatchHistoryDomainNameResolutionPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

val checkWatchHistoryDomainNameResolutionPatch = checkWatchHistoryDomainNameResolutionPatch(
    block = {
        dependsOn(sharedExtensionPatch)

        compatibleWith(COMPATIBILITY_YOUTUBE)
    },
    mainActivityFingerprint = YouTubeActivityOnCreateFingerprint
)
