/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.patches.youtube.misc.sharesheet

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.litho.filter.addLithoFilter
import app.silva.patches.youtube.misc.litho.filter.lithoFilterPatch
import app.silva.patches.youtube.misc.recyclerviewtree.hook.addRecyclerViewTreeHook
import app.silva.patches.youtube.misc.recyclerviewtree.hook.recyclerViewTreeHookPatch
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/OpenSystemShareSheetPatch;"

private const val FILTER_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/components/OpenSystemShareSheetFilter;"

@Suppress("unused")
internal fun openSystemShareSheetPatch(
) = bytecodePatch(
    name = "Open system share sheet",
    description = "Adds an option to always open the system share sheet instead of the in-app share sheet."
) {

    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        lithoFilterPatch,
        recyclerViewTreeHookPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.MISC.addPreferences(
            SwitchPreference("silva_open_system_share_sheet")
        )

        addRecyclerViewTreeHook(EXTENSION_CLASS_DESCRIPTOR)

        QueryIntentListFingerprint.method.apply {

            addInstructions(
                0,
                """
                    invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->openSystemShareSheetEnabled()Z
                    move-result v0
                    if-eqz v0, :ignore
                    new-instance v0, Ljava/util/ArrayList;
                    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V
                    return-object v0
                    :ignore
                    nop
                """
            )
        }

        addLithoFilter(FILTER_CLASS_DESCRIPTOR)
    }
}