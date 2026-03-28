package app.silva.patches.youtube.misc.check

import app.silva.patches.shared.misc.checks.checkEnvironmentPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

internal val checkEnvironmentPatch = checkEnvironmentPatch(
    mainActivityOnCreateFingerprint = YouTubeActivityOnCreateFingerprint,
    extensionPatch = sharedExtensionPatch,
    "com.google.android.youtube",
)
