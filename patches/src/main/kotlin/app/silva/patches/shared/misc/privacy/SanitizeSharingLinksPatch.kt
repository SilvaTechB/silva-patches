package app.silva.patches.shared.misc.privacy

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.BytecodePatchBuilder
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.BasePreferenceScreen
import app.silva.patches.shared.misc.settings.preference.PreferenceCategory
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.util.addInstructionsAtControlFlowLabel
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/shared/patches/SanitizeSharingLinksPatch;"

/**
 * Patch shared by YouTube and YT Music.
 */
internal fun sanitizeSharingLinksPatch(
    block: BytecodePatchBuilder.() -> Unit = {},
    executeBlock: BytecodePatchContext.() -> Unit = {},
    preferenceScreen: BasePreferenceScreen.Screen,
    replaceMusicLinksWithYouTube: Boolean = false
) = bytecodePatch(
    name = "Sanitize sharing links",
    description = "Removes the tracking query parameters from shared links.",
) {
    block()

    execute {
        executeBlock()

        val sanitizePreference = SwitchPreference("silva_sanitize_sharing_links")

        preferenceScreen.addPreferences(
            if (replaceMusicLinksWithYouTube) {
                PreferenceCategory(
                    titleKey = null,
                    sorting = Sorting.UNSORTED,
                    tag = "app.silva.extension.shared.settings.preference.NoTitlePreferenceCategory",
                    preferences = setOf(
                        sanitizePreference,
                        SwitchPreference("silva_replace_music_with_youtube")
                    )
                )
            } else {
                sanitizePreference
            }
        )

        fun Fingerprint.hookUrlString(matchIndex: Int) {
            val index = instructionMatches[matchIndex].index
            val urlRegister = method.getInstruction<OneRegisterInstruction>(index).registerA

            method.addInstructions(
                index + 1,
                """
                    invoke-static { v$urlRegister }, $EXTENSION_CLASS_DESCRIPTOR->sanitize(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$urlRegister
                """
            )
        }

        fun Fingerprint.hookIntentPutExtra(matchIndex: Int) {
            val index = instructionMatches[matchIndex].index
            val urlRegister = method.getInstruction<FiveRegisterInstruction>(index).registerE

            method.addInstructionsAtControlFlowLabel(
                index,
                """
                    invoke-static { v$urlRegister }, $EXTENSION_CLASS_DESCRIPTOR->sanitize(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$urlRegister
                """
            )
        }

        // YouTube share sheet copy link.
        YouTubeCopyTextFingerprint.hookUrlString(0)

        // YouTube share sheet other apps.
        YouTubeShareSheetFingerprint.hookIntentPutExtra(3)

        // Native system share sheet.
        YouTubeSystemShareSheetFingerprint.hookIntentPutExtra(3)
    }
}