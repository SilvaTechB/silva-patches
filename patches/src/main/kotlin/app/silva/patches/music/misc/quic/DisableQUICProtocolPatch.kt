package app.silva.patches.music.misc.quic

import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.settings.PreferenceScreen
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.shared.misc.quic.disableQUICProtocolPatch

@Suppress("unused")
val disableQUICProtocolPatchMusic = disableQUICProtocolPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },
    preferenceScreen = PreferenceScreen.MISC
)