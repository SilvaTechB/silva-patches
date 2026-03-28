/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.silva.patches.youtube.layout.player.fullscreen

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.ListPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playercontrols.playerControlsPatch
import app.silva.patches.youtube.misc.playertype.playerTypeHookPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.video.information.playerStatusMethodRef
import app.silva.patches.youtube.video.information.videoInformationPatch
import app.silva.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode

@Suppress("unused")
internal val exitFullscreenPatch = bytecodePatch(
    name = "Exit fullscreen mode",
    description = "Adds options to automatically exit fullscreen mode when a video reaches the end."
) {

    compatibleWith(COMPATIBILITY_YOUTUBE)

    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        playerTypeHookPatch,
        playerControlsPatch,
        videoInformationPatch
    )

    // Cannot declare as top level since this patch is in the same package as
    // other patches that declare same constant name with internal visibility.
    @Suppress("LocalVariableName")
    val EXTENSION_CLASS_DESCRIPTOR =
        "Lapp/morphe/extension/youtube/patches/ExitFullscreenPatch;"

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            ListPreference("silva_exit_fullscreen")
        )

        playerStatusMethodRef.get()!!.apply {
            val insertIndex =
                indexOfFirstInstructionOrThrow(Opcode.SGET_OBJECT) + 1

            addInstruction(
                insertIndex,
                "invoke-static/range { p1 .. p1 }, $EXTENSION_CLASS_DESCRIPTOR->endOfVideoReached(Ljava/lang/Enum;)V",
            )
        }
    }
}
