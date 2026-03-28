package app.silva.patches.youtube.video.audio

import app.silva.patches.shared.misc.audio.tracks.forceOriginalAudioPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playservice.is_20_07_or_greater
import app.silva.patches.youtube.misc.playservice.versionCheckPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

@Suppress("unused")
val forceOriginalAudioPatch = forceOriginalAudioPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE)
    },
    fixUseLocalizedAudioTrackFlag = { is_20_07_or_greater },
    mainActivityOnCreateFingerprint = YouTubeActivityOnCreateFingerprint,
    subclassExtensionClassDescriptor = "Lapp/silva/extension/youtube/patches/ForceOriginalAudioPatch;",
    preferenceScreen = PreferenceScreen.VIDEO,
)
