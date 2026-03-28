package app.silva.patches.youtube.misc.backgroundplayback

import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.fix.bitmap.fixRecycledBitmapPatch
import app.silva.patches.shared.misc.mapping.ResourceType
import app.silva.patches.shared.misc.mapping.getResourceId
import app.silva.patches.shared.misc.mapping.resourceMappingPatch
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playertype.playerTypeHookPatch
import app.silva.patches.youtube.misc.playservice.is_19_34_or_greater
import app.silva.patches.youtube.misc.playservice.is_20_29_or_greater
import app.silva.patches.youtube.misc.playservice.versionCheckPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.BackgroundPlaybackManagerShortsFingerprint
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.video.information.videoInformationPatch
import app.silva.util.addInstructionsAtControlFlowLabel
import app.silva.util.findInstructionIndicesReversedOrThrow
import app.silva.util.getReference
import app.silva.util.insertLiteralOverride
import app.silva.util.returnEarly
import app.silva.util.returnLate
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal var prefBackgroundAndOfflineCategoryId = -1L
    private set

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/youtube/patches/BackgroundPlaybackPatch;"

val backgroundPlaybackPatch = bytecodePatch(
    name = "Remove background playback restrictions",
    description = "Removes restrictions on background playback, including playing kids videos in the background.",
) {
    dependsOn(
        resourceMappingPatch,
        sharedExtensionPatch,
        playerTypeHookPatch,
        videoInformationPatch,
        settingsPatch,
        versionCheckPatch,
        fixRecycledBitmapPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.SHORTS.addPreferences(
            SwitchPreference("silva_shorts_disable_background_playback")
        )

        prefBackgroundAndOfflineCategoryId = getResourceId(
            ResourceType.STRING,
            "pref_background_and_offline_category"
        )

        arrayOf(
            BackgroundPlaybackManagerFingerprint to "isBackgroundPlaybackAllowed",
            BackgroundPlaybackManagerShortsFingerprint to "isBackgroundShortsPlaybackAllowed",
        ).forEach { (fingerprint, integrationsMethod) ->
            fingerprint.method.apply {
                findInstructionIndicesReversedOrThrow(Opcode.RETURN).forEach { index ->
                    val register = getInstruction<OneRegisterInstruction>(index).registerA

                    addInstructionsAtControlFlowLabel(
                        index,
                        """
                            invoke-static { v$register }, $EXTENSION_CLASS_DESCRIPTOR->$integrationsMethod(Z)Z
                            move-result v$register 
                        """
                    )
                }
            }
        }

        // Enable background playback option in YouTube settings
        BackgroundPlaybackSettingsFingerprint.originalMethod.apply {
            val booleanCalls = instructions.withIndex().filter {
                it.value.getReference<MethodReference>()?.returnType == "Z"
            }

            val settingsBooleanIndex = booleanCalls.elementAt(1).index
            val settingsBooleanMethod by navigate(this).to(settingsBooleanIndex)

            settingsBooleanMethod.returnEarly(true)
        }

        // Force allowing background play for Shorts.
        ShortsBackgroundPlaybackFeatureFlagFingerprint.method.returnEarly(true)

        // Force allowing background play for videos labeled for kids.
        KidsBackgroundPlaybackPolicyControllerFingerprint.method.returnEarly()

        // Fix PiP buttons not working after locking/unlocking device screen.
        if (is_19_34_or_greater) {
            PipInputConsumerFeatureFlagFingerprint.let {
                it.method.insertLiteralOverride(
                    it.instructionMatches.first().index,
                    false
                )
            }
        }

        if (is_20_29_or_greater) {
            // Client flag that interferes with background playback of some video types.
            // Exact purpose is not clear and it's used in ~ 100 locations.
            NewPlayerTypeEnumFeatureFlag.method.returnLate(false)
        }
    }
}
