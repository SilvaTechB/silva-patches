package app.silva.patches.youtube.layout.hide.rollingnumber

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.RollingNumberTextViewAnimationUpdateFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/DisableRollingNumberAnimationsPatch;"

val disableRollingNumberAnimationPatch = bytecodePatch(
    name = "Disable rolling number animations",
    description = "Adds an option to disable rolling number animations of video view count, user likes, and upload time.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            SwitchPreference("silva_disable_rolling_number_animations"),
        )

        // Animations are disabled by preventing an Image from being applied to the text span,
        // which prevents the animations from appearing.
        RollingNumberTextViewAnimationUpdateFingerprint.let {
            val blockStartIndex = it.instructionMatches.first().index
            val blockEndIndex = it.instructionMatches.last().index + 1
            it.method.apply {
                val freeRegister = getInstruction<OneRegisterInstruction>(blockStartIndex).registerA

                // ReturnYouTubeDislike also makes changes to this same method,
                // and must add control flow label to a noop instruction to
                // ensure RYD patch adds its changes after the control flow label.
                addInstructions(blockEndIndex, "nop")

                addInstructionsWithLabels(
                    blockStartIndex,
                    """
                        invoke-static { }, $EXTENSION_CLASS_DESCRIPTOR->disableRollingNumberAnimations()Z
                        move-result v$freeRegister
                        if-nez v$freeRegister, :disable_animations
                    """,
                    ExternalLabel("disable_animations", getInstruction(blockEndIndex)),
                )
            }
        }
    }
}
