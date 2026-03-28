package app.silva.patches.music.layout.branding

import app.morphe.patcher.Fingerprint
import app.silva.patches.music.shared.YOUTUBE_MUSIC_MAIN_ACTIVITY_CLASS_TYPE
import app.silva.patches.shared.misc.mapping.ResourceType
import app.silva.patches.shared.misc.mapping.resourceLiteral

internal object CairoSplashAnimationConfigFingerprint : Fingerprint(
    definingClass = YOUTUBE_MUSIC_MAIN_ACTIVITY_CLASS_TYPE,
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;"),
    filters = listOf(
        resourceLiteral(ResourceType.LAYOUT, "main_activity_launch_animation")
    )
)
