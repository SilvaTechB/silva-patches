package app.silva.patches.music.layout.compactheader

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.settings.PreferenceScreen
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.shared.misc.mapping.ResourceType
import app.silva.patches.shared.misc.mapping.getResourceId
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

internal var chipCloud = -1L
    private set

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/music/patches/HideCategoryBarPatch;"

@Suppress("unused")
val hideCategoryBar = bytecodePatch(
    name = "Hide category bar",
    description = "Adds an option to hide the category bar at the top of the homepage."
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("silva_music_hide_category_bar"),
        )

        chipCloud = getResourceId(ResourceType.LAYOUT, "chip_cloud")

        ChipCloudFingerprint.method.apply {
            val targetIndex = ChipCloudFingerprint.instructionMatches.last().index
            val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

            addInstruction(
                targetIndex + 1,
                "invoke-static { v$targetRegister }, $EXTENSION_CLASS_DESCRIPTOR->hideCategoryBar(Landroid/view/View;)V"
            )
        }
    }
}
