package app.silva.patches.youtube.misc.extension

import app.silva.patches.shared.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.extension.hooks.applicationInitHook
import app.silva.patches.youtube.misc.extension.hooks.applicationInitOnCrateHook

val sharedExtensionPatch = sharedExtensionPatch(
    "youtube",
    true,
    applicationInitHook,
    applicationInitOnCrateHook
)
