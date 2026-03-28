package app.silva.patches.music.misc.extension

import app.silva.patches.music.misc.extension.hooks.youTubeMusicApplicationInitHook
import app.silva.patches.music.misc.extension.hooks.youTubeMusicApplicationInitOnCreateHook
import app.silva.patches.shared.misc.extension.sharedExtensionPatch

val sharedExtensionPatch = sharedExtensionPatch(
    "music",
    true,
    youTubeMusicApplicationInitHook,
    youTubeMusicApplicationInitOnCreateHook
)

