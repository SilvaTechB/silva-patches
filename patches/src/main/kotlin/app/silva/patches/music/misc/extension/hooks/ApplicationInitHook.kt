package app.silva.patches.music.misc.extension.hooks

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import app.silva.patches.music.shared.YOUTUBE_MUSIC_MAIN_ACTIVITY_CLASS_TYPE
import app.silva.patches.shared.misc.extension.ExtensionHook
import app.silva.patches.shared.misc.extension.activityOnCreateExtensionHook

internal val youTubeMusicApplicationInitHook = ExtensionHook(
    Fingerprint(
        name = "onCreate",
        returnType = "V",
        parameters = listOf(),
        filters = listOf(
            string("activity")
        )
    )
)

internal val youTubeMusicApplicationInitOnCreateHook = activityOnCreateExtensionHook(
    YOUTUBE_MUSIC_MAIN_ACTIVITY_CLASS_TYPE
)
