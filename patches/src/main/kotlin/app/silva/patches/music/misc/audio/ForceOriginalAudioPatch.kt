package app.silva.patches.music.misc.audio

import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.playservice.is_8_05_or_greater
import app.silva.patches.music.misc.playservice.versionCheckPatch
import app.silva.patches.music.misc.settings.PreferenceScreen
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.music.shared.MusicActivityOnCreateFingerprint
import app.silva.patches.shared.misc.audio.tracks.forceOriginalAudioPatch

@Suppress("unused")
val forceOriginalAudioPatch = forceOriginalAudioPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },
    fixUseLocalizedAudioTrackFlag = { is_8_05_or_greater },
    mainActivityOnCreateFingerprint = MusicActivityOnCreateFingerprint,
    subclassExtensionClassDescriptor = "Lapp/silva/extension/music/patches/ForceOriginalAudioPatch;",
    preferenceScreen = PreferenceScreen.MISC,
)
