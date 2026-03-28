package app.silva.patches.music.ad.video

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.settings.PreferenceScreen
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.util.getMutableMethod
import app.silva.util.getReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/music/patches/HideVideoAdsPatch;"

@Suppress("unused")
val hideVideoAdsPatch = bytecodePatch(
    name = "Hide music video ads",
    description = "Adds an option to hide ads that appear while listening to or streaming music videos, podcasts, or songs.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        PreferenceScreen.ADS.addPreferences(
            SwitchPreference("silva_music_hide_video_ads"),
        )

        ShowVideoAdsFingerprint.instructionMatches[1]
            .getInstruction<ReferenceInstruction>()
            .getReference<MethodReference>()!!
            .getMutableMethod()
            .addInstructions(
                0,
                """
                    invoke-static { p1 }, $EXTENSION_CLASS_DESCRIPTOR->showVideoAds(Z)Z
                    move-result p1
                """
            )
    }
}
