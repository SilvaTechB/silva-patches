package app.silva.patches.music.misc.backgroundplayback

import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.shared.misc.fix.bitmap.fixRecycledBitmapPatch
import app.silva.util.returnEarly

val backgroundPlaybackPatch = bytecodePatch(
    name = "Remove background playback restrictions",
    description = "Removes restrictions on background playback, including playing kids videos in the background.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        fixRecycledBitmapPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        KidsBackgroundPlaybackPolicyControllerFingerprint.method.returnEarly()

        BackgroundPlaybackDisableFingerprint.method.returnEarly(true)
    }
}
