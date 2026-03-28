/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.silva.patches.youtube.video.quality

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.ListPreference
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playertype.playerTypeHookPatch
import app.silva.patches.youtube.misc.playservice.versionCheckPatch
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.VideoQualityChangedFingerprint
import app.silva.patches.youtube.video.information.onCreateHook
import app.silva.patches.youtube.video.information.videoInformationPatch
import app.silva.util.findFieldFromToString
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/playback/quality/RememberVideoQualityPatch;"

val rememberVideoQualityPatch = bytecodePatch {
    dependsOn(
        sharedExtensionPatch,
        videoInformationPatch,
        playerTypeHookPatch,
        settingsPatch,
        versionCheckPatch,
    )

    execute {
        settingsMenuVideoQualityGroup.addAll(listOf(
            ListPreference(
                key = "silva_video_quality_default_mobile",
                entriesKey = "silva_video_quality_default_entries",
                entryValuesKey = "silva_video_quality_default_entry_values"
            ),
            ListPreference(
                key = "silva_video_quality_default_wifi",
                entriesKey = "silva_video_quality_default_entries",
                entryValuesKey = "silva_video_quality_default_entry_values"
            ),
            SwitchPreference("silva_remember_video_quality_last_selected"),

            ListPreference(
                key = "silva_shorts_quality_default_mobile",
                entriesKey = "silva_shorts_quality_default_entries",
                entryValuesKey = "silva_shorts_quality_default_entry_values",
            ),
            ListPreference(
                key = "silva_shorts_quality_default_wifi",
                entriesKey = "silva_shorts_quality_default_entries",
                entryValuesKey = "silva_shorts_quality_default_entry_values"
            ),
            SwitchPreference("silva_remember_shorts_quality_last_selected"),
            SwitchPreference("silva_remember_video_quality_last_selected_toast")
        ))

        onCreateHook(EXTENSION_CLASS_DESCRIPTOR, "newVideoStarted")

        val initialResolutionField = PlaybackStartParametersToStringFingerprint.method
                .findFieldFromToString(FIXED_RESOLUTION_STRING)

        val playbackStartParametersConstructorFingerprint = Fingerprint(
            classFingerprint = PlaybackStartParametersToStringFingerprint,
            name = "<init>",
            filters = listOf(
                fieldAccess(
                    opcode = Opcode.IPUT_OBJECT,
                    reference = initialResolutionField
                )
            )
        )

        // Inject a call to override initial video quality.
        playbackStartParametersConstructorFingerprint.let {
            it.method.apply {
                val index = it.instructionMatches.last().index
                val register = getInstruction<TwoRegisterInstruction>(index).registerA

                addInstructions(
                    index,
                    """
                        invoke-static { v$register }, $EXTENSION_CLASS_DESCRIPTOR->getInitialVideoQuality(Lj$/util/Optional;)Lj$/util/Optional;
                        move-result-object v$register
                    """
                )
            }
        }

        // Inject a call to remember the selected quality for Shorts.
        VideoQualityItemOnClickFingerprint.method.addInstruction(
            0,
            "invoke-static { p3 }, $EXTENSION_CLASS_DESCRIPTOR->userChangedShortsQuality(I)V"
        )

        // Inject a call to remember the user selected quality for regular videos.
        VideoQualityChangedFingerprint.let {
            it.method.apply {
                val index = it.instructionMatches.last().index
                val register = getInstruction<TwoRegisterInstruction>(index).registerA

                addInstruction(
                    index + 1,
                    "invoke-static { v$register }, $EXTENSION_CLASS_DESCRIPTOR->userChangedQuality(I)V",
                )
            }
        }
    }
}
