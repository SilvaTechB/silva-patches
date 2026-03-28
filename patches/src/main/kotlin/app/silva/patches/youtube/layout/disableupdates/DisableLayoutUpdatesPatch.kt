/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.patches.youtube.layout.disableupdates

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/DisableLayoutUpdatesPatch;"

@Suppress("unused")
val disableLayoutUpdatesPatch = bytecodePatch(
    name = "Disable layout updates",
    description = "Adds an option to disable server side layout updates and use an older UI.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("silva_disable_layout_updates")
        )

        CronetHeaderFingerprint.let {
            it.method.apply {
                val index = it.instructionMatches.first().index

                addInstructions(
                    index,
                    """
                        invoke-static { p1, p2 }, $EXTENSION_CLASS_DESCRIPTOR->disableLayoutUpdates(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p2
                    """
                )
            }
        }
    }
}
