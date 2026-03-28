/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.patches.reddit.layout.communities

import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.reddit.misc.settings.settingsPatch
import app.silva.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import app.silva.util.setExtensionIsPatchIncluded

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/reddit/patches/HideRecommendedCommunitiesShelf;"

@Suppress("unused")
val hideRecommendedCommunitiesShelf = bytecodePatch(
    name = "Hide recommended communities shelf",
    description = "Adds an option to hide the recommended communities shelves in subreddits."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(settingsPatch)

    execute {
        CommunityRecommendationSectionFingerprint.method.addInstructionsWithLabels(
            0,
            """
                invoke-static { }, $EXTENSION_CLASS_DESCRIPTOR->hideRecommendedCommunitiesShelf()Z
                move-result v0
                if-eqz v0, :off
                return-void
                :off
                nop
            """
        )

        setExtensionIsPatchIncluded(EXTENSION_CLASS_DESCRIPTOR)
    }
}
