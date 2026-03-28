package app.silva.patches.music.misc.settings

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.silva.patches.all.misc.packagename.setOrGetFallbackPackageName
import app.silva.patches.all.misc.resources.addAppResources
import app.silva.patches.all.misc.resources.addResourcesPatch
import app.silva.patches.all.misc.resources.localesYouTube
import app.silva.patches.all.misc.resources.setAddResourceLocale
import app.silva.patches.music.misc.extension.hooks.youTubeMusicApplicationInitOnCreateHook
import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.gms.Constants.MUSIC_PACKAGE_NAME
import app.silva.patches.music.misc.playservice.is_8_40_or_greater
import app.silva.patches.music.misc.playservice.versionCheckPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.shared.BoldIconsFeatureFlagFingerprint
import app.silva.patches.shared.GoogleApiActivityOnCreateFingerprint
import app.silva.patches.shared.misc.checks.experimentalAppNoticePatch
import app.silva.patches.shared.misc.initialization.initializationPatch
import app.silva.patches.shared.misc.mapping.resourceMappingPatch
import app.silva.patches.shared.misc.settings.SILVA_SETTINGS_INTENT
import app.silva.patches.shared.misc.settings.preference.BasePreference
import app.silva.patches.shared.misc.settings.preference.BasePreferenceScreen
import app.silva.patches.shared.misc.settings.preference.InputType
import app.silva.patches.shared.misc.settings.preference.IntentPreference
import app.silva.patches.shared.misc.settings.preference.NonInteractivePreference
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.shared.misc.settings.preference.TextPreference
import app.silva.patches.shared.misc.settings.settingsPatch
import app.silva.patches.youtube.misc.settings.modifyActivityForSettingsInjection
import app.silva.util.copyXmlNode
import app.silva.util.inputStreamFromBundledResource
import app.silva.util.insertLiteralOverride

private const val MUSIC_ACTIVITY_HOOK_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/music/settings/MusicActivityHook;"

private val preferences = mutableSetOf<BasePreference>()

private val settingsResourcePatch = resourcePatch {
    dependsOn(
        resourceMappingPatch,
        settingsPatch(
            rootPreferences = listOf(
                IntentPreference(
                    titleKey = "silva_settings_title",
                    summaryKey = null,
                    intent = newIntent(SILVA_SETTINGS_INTENT),
                ) to "settings_headers"
            ),
            preferences = preferences
        )
    )

    execute {
        // Set the style for the Morphe settings to follow the style of the music settings,
        // namely: action bar height, menu item padding and remove horizontal dividers.
        val targetResource = "values/styles.xml"
        inputStreamFromBundledResource(
            "settings/music",
            targetResource,
        )!!.let { inputStream ->
            "resources".copyXmlNode(
                document(inputStream),
                document("res/$targetResource"),
            ).close()
        }

        // Remove horizontal dividers from the music settings.
        val styleFile = get("res/values/styles.xml")
        styleFile.writeText(
            styleFile.readText()
                .replace(
                    "allowDividerAbove\">true",
                    "allowDividerAbove\">false"
                ).replace(
                    "allowDividerBelow\">true",
                    "allowDividerBelow\">false"
                )
        )
    }
}

val settingsPatch = bytecodePatch(
    description = "Adds settings for Morphe to YouTube Music.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsResourcePatch,
        addResourcesPatch,
        versionCheckPatch,
        experimentalAppNoticePatch(
            mainActivityFingerprint = youTubeMusicApplicationInitOnCreateHook.fingerprint,
            recommendedAppVersion = COMPATIBILITY_YOUTUBE_MUSIC.targets.first { !it.isExperimental }.version!!
        ),
        initializationPatch(
            mainActivityFingerprint = youTubeMusicApplicationInitOnCreateHook.fingerprint
        )
    )

    execute {
        setAddResourceLocale(localesYouTube)
        addAppResources("shared-youtube")
        addAppResources("music")

        // Add an "About" preference to the top.
        preferences += NonInteractivePreference(
            key = "silva_settings_music_screen_0_about",
            summaryKey = null,
            tag = "app.silva.extension.shared.settings.preference.about.SilvaAboutPreference",
            selectable = true,
        )

        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("silva_settings_search_history"),
        )

        PreferenceScreen.MISC.addPreferences(
            TextPreference(
                key = null,
                titleKey = "silva_pref_import_export_title",
                summaryKey = "silva_pref_import_export_summary",
                inputType = InputType.TEXT_MULTI_LINE,
                tag = "app.silva.extension.shared.settings.preference.ImportExportPreference",
            )
        )

        modifyActivityForSettingsInjection(
            GoogleApiActivityOnCreateFingerprint,
            MUSIC_ACTIVITY_HOOK_CLASS_DESCRIPTOR,
            true
        )

        // TODO: Implement a 'Spoof app version' patch for YouTube Music.
        if (is_8_40_or_greater) {
            BoldIconsFeatureFlagFingerprint.let {
                it.method.insertLiteralOverride(
                    it.instructionMatches.first().index,
                    "$MUSIC_ACTIVITY_HOOK_CLASS_DESCRIPTOR->useBoldIcons(Z)Z"
                )
            }
        }
    }

    finalize {
        PreferenceScreen.close()
    }
}

/**
 * Creates an intent to open Morphe settings.
 */
fun newIntent(settingsName: String) = IntentPreference.Intent(
    data = settingsName,
    targetClass = "com.google.android.gms.common.api.GoogleApiActivity"
) {
    // The package name change has to be reflected in the intent.
    setOrGetFallbackPackageName(MUSIC_PACKAGE_NAME)
}

object PreferenceScreen : BasePreferenceScreen() {
    val ADS = Screen(
        key = "silva_settings_music_screen_1_ads",
        summaryKey = null
    )
    val GENERAL = Screen(
        key = "silva_settings_music_screen_2_general",
        summaryKey = null
    )
    val PLAYER = Screen(
        key = "silva_settings_music_screen_3_player",
        summaryKey = null
    )
    val MISC = Screen(
        key = "silva_settings_music_screen_4_misc",
        summaryKey = null
    )

    override fun commit(screen: PreferenceScreenPreference) {
        preferences += screen
    }
}
