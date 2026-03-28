/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.patches.reddit.layout.viewcount

import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.reddit.misc.flag.featureFlagHookPatch
import app.silva.patches.reddit.misc.flag.hookFeatureFlag
import app.silva.patches.reddit.misc.settings.settingsPatch
import app.silva.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import app.silva.util.setExtensionIsPatchIncluded

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/reddit/patches/ShowViewCountPatch;"

@Suppress("unused")
val showViewCountPatch = bytecodePatch(
    name = "Show view count",
    description = "Adds an option to show the view count of Posts."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(
        settingsPatch,
        featureFlagHookPatch
    )

    execute {

        hookFeatureFlag("$EXTENSION_CLASS_DESCRIPTOR->showViewCount")

        setExtensionIsPatchIncluded(EXTENSION_CLASS_DESCRIPTOR)
    }
}
