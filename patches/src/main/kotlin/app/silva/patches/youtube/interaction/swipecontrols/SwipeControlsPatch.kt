package app.silva.patches.youtube.interaction.swipecontrols

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.silva.patches.shared.misc.settings.preference.InputType
import app.silva.patches.shared.misc.settings.preference.ListPreference
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.shared.misc.settings.preference.TextPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playertype.playerTypeHookPatch
import app.silva.patches.youtube.misc.playservice.is_19_43_or_greater
import app.silva.patches.youtube.misc.playservice.is_20_22_or_greater
import app.silva.patches.youtube.misc.playservice.is_20_34_or_greater
import app.silva.patches.youtube.misc.playservice.versionCheckPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeMainActivityConstructorFingerprint
import app.silva.util.ResourceGroup
import app.silva.util.copyResources
import app.silva.util.insertLiteralOverride
import app.silva.util.transformMethods
import app.silva.util.traverseClassHierarchy
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

internal const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/youtube/swipecontrols/SwipeControlsHostActivity;"

private val swipeControlsResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        versionCheckPatch,
    )

    execute {
        // If fullscreen swipe is enabled in newer versions the app can crash.
        // It likely is caused by conflicting experimental flags that are never enabled together.
        // Flag was completely removed in 20.34+
        if (is_19_43_or_greater && !is_20_22_or_greater) {
            PreferenceScreen.SWIPE_CONTROLS.addPreferences(
                SwitchPreference("silva_swipe_change_video")
            )
        }

        PreferenceScreen.SWIPE_CONTROLS.addPreferences(
            SwitchPreference("silva_swipe_brightness"),
            SwitchPreference("silva_swipe_volume"),
            SwitchPreference("silva_swipe_press_to_engage"),
            SwitchPreference("silva_swipe_haptic_feedback"),
            SwitchPreference("silva_swipe_save_and_restore_brightness"),
            SwitchPreference("silva_swipe_lowest_value_enable_auto_brightness"),
            ListPreference("silva_swipe_overlay_style"),
            TextPreference("silva_swipe_overlay_background_opacity", inputType = InputType.NUMBER),
            TextPreference("silva_swipe_overlay_progress_brightness_color",
                tag = "app.silva.extension.shared.settings.preference.ColorPickerWithOpacitySliderPreference",
                inputType = InputType.TEXT_CAP_CHARACTERS),
            TextPreference("silva_swipe_overlay_progress_volume_color",
                tag = "app.silva.extension.shared.settings.preference.ColorPickerWithOpacitySliderPreference",
                inputType = InputType.TEXT_CAP_CHARACTERS),
            TextPreference("silva_swipe_text_overlay_size", inputType = InputType.NUMBER),
            TextPreference("silva_swipe_overlay_timeout", inputType = InputType.NUMBER),
            TextPreference("silva_swipe_threshold", inputType = InputType.NUMBER),
            TextPreference("silva_swipe_volume_sensitivity", inputType = InputType.NUMBER),
        )

        copyResources(
            "swipecontrols",
            ResourceGroup(
                "drawable",
                "silva_ic_sc_brightness_auto.xml",
                "silva_ic_sc_brightness_full.xml",
                "silva_ic_sc_brightness_high.xml",
                "silva_ic_sc_brightness_low.xml",
                "silva_ic_sc_brightness_medium.xml",
                "silva_ic_sc_volume_high.xml",
                "silva_ic_sc_volume_low.xml",
                "silva_ic_sc_volume_mute.xml",
                "silva_ic_sc_volume_normal.xml",
            ),
        )
    }
}

@Suppress("unused")
val swipeControlsPatch = bytecodePatch(
    name = "Swipe controls",
    description = "Adds options to enable and configure volume and brightness swipe controls.",
) {
    dependsOn(
        sharedExtensionPatch,
        playerTypeHookPatch,
        swipeControlsResourcePatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        val wrapperClass = SwipeControlsHostActivityFingerprint.classDef
        val targetClass = YouTubeMainActivityConstructorFingerprint.classDef

        // Inject the wrapper class from the extension into the class hierarchy of MainActivity.
        wrapperClass.setSuperClass(targetClass.superclass)
        targetClass.setSuperClass(wrapperClass.type)

        // Ensure all classes and methods in the hierarchy are non-final, so we can override them in the extension.
        traverseClassHierarchy(targetClass) {
            accessFlags = accessFlags and AccessFlags.FINAL.value.inv()
            transformMethods {
                ImmutableMethod(
                    definingClass,
                    name,
                    parameters,
                    returnType,
                    accessFlags and AccessFlags.FINAL.value.inv(),
                    annotations,
                    hiddenApiRestrictions,
                    implementation,
                ).toMutable()
            }
        }

        // region patch to enable/disable swipe to change video.

        if (is_19_43_or_greater && !is_20_34_or_greater) {
            SwipeChangeVideoFingerprint.let {
                it.method.insertLiteralOverride(
                    it.instructionMatches.last().index,
                    "$EXTENSION_CLASS_DESCRIPTOR->allowSwipeChangeVideo(Z)Z"
                )
            }
        }

        // endregion
    }
}
