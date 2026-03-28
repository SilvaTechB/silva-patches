package app.silva.patches.music.misc.dns

import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.music.shared.MusicActivityOnCreateFingerprint
import app.silva.patches.shared.misc.dns.checkWatchHistoryDomainNameResolutionPatch

val checkWatchHistoryDomainNameResolutionPatch = checkWatchHistoryDomainNameResolutionPatch(
    block = {
        dependsOn(
            sharedExtensionPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },

    mainActivityFingerprint = MusicActivityOnCreateFingerprint
)
