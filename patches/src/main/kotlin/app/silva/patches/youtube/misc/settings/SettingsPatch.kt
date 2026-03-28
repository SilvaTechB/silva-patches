/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.silva.patches.youtube.misc.settings

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.silva.patches.all.misc.packagename.setOrGetFallbackPackageName
import app.silva.patches.all.misc.resources.addAppResources
import app.silva.patches.all.misc.resources.addResourcesPatch
import app.silva.patches.all.misc.resources.localesYouTube
import app.silva.patches.all.misc.resources.setAddResourceLocale
import app.silva.patches.shared.BoldIconsFeatureFlagFingerprint
import app.silva.patches.shared.GoogleApiActivityOnCreateFingerprint
import app.silva.patches.shared.layout.branding.addLicensePatch
import app.silva.patches.shared.misc.checks.experimentalAppNoticePatch
import app.silva.patches.shared.misc.initialization.initializationPatch
import app.silva.patches.shared.misc.mapping.resourceMappingPatch
import app.silva.patches.shared.misc.settings.SILVA_SETTINGS_INTENT
import app.silva.patches.shared.misc.settings.overrideThemeColors
import app.silva.patches.shared.misc.settings.preference.BasePreference
import app.silva.patches.shared.misc.settings.preference.BasePreferenceScreen
import app.silva.patches.shared.misc.settings.preference.InputType
import app.silva.patches.shared.misc.settings.preference.IntentPreference
import app.silva.patches.shared.misc.settings.preference.ListPreference
import app.silva.patches.shared.misc.settings.preference.NonInteractivePreference
import app.silva.patches.shared.misc.settings.preference.PreferenceCategory
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.shared.misc.settings.preference.TextPreference
import app.silva.patches.shared.misc.settings.settingsPatch
import app.silva.patches.youtube.misc.check.checkEnvironmentPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.fix.contentprovider.fixContentProviderPatch
import app.silva.patches.youtube.misc.fix.likebutton.fixLikeButtonPatch
import app.silva.patches.youtube.misc.fix.playbackspeed.fixPlaybackSpeedWhilePlayingPatch
import app.silva.patches.youtube.misc.playservice.is_19_34_or_greater
import app.silva.patches.youtube.misc.playservice.is_20_31_or_greater
import app.silva.patches.youtube.misc.playservice.versionCheckPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint
import app.silva.util.ResourceGroup
import app.silva.util.addInstructionsAtControlFlowLabel
import app.silva.util.copyResources
import app.silva.util.findElementByAttributeValueOrThrow
import app.silva.util.findInstructionIndicesReversedOrThrow
import app.silva.util.insertLiteralOverride
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter
import com.android.tools.smali.dexlib2.util.MethodUtil

private const val BASE_ACTIVITY_HOOK_CLASS_DESCRIPTOR = "Lapp/silva/extension/shared/settings/BaseActivityHook;"
private const val YOUTUBE_ACTIVITY_HOOK_CLASS_DESCRIPTOR = "Lapp/silva/extension/youtube/settings/YouTubeActivityHook;"

private val preferences = mutableSetOf<BasePreference>()

private val settingsResourcePatch = resourcePatch {
    dependsOn(
        resourceMappingPatch,
        settingsPatch(
            rootPreferences = listOf(
                IntentPreference(
                    titleKey = "silva_settings_title",
                    summaryKey = null,
                    intent = newIntent(SILVA_SETTINGS_INTENT)
                ) to "settings_fragment",

                PreferenceCategory(
                    titleKey = "silva_settings_title",
                    layout = "@layout/preference_group_title",
                    preferences = setOf(
                        IntentPreference(
                            titleKey = "silva_settings_submenu_title",
                            summaryKey = null,
                            icon = "@drawable/silva_settings_icon_dynamic",
                            layout = "@layout/preference_with_icon",
                            intent = newIntent(SILVA_SETTINGS_INTENT)
                        )
                    )
                ) to "settings_fragment_cairo"
            ),
            preferences = preferences
        )
    )

    execute {
        // Use same colors as stock YouTube.
        overrideThemeColors("@color/yt_white1", "@color/yt_black3")

        copyResources(
            "settings",
            ResourceGroup("drawable",
                "silva_settings_icon_dynamic.xml",
                "silva_settings_icon_light.xml",
                "silva_settings_icon_bold_light.xml",
                "silva_settings_icon_dark.xml",
                "silva_settings_icon_bold_dark.xml",
                "silva_settings_screen_00_about.xml",
                "silva_settings_screen_00_about_bold.xml",
                "silva_settings_screen_01_ads.xml",
                "silva_settings_screen_01_ads_bold.xml",
                "silva_settings_screen_02_alt_thumbnails.xml",
                "silva_settings_screen_02_alt_thumbnails_bold.xml",
                "silva_settings_screen_03_feed.xml",
                "silva_settings_screen_03_feed_bold.xml",
                "silva_settings_screen_04_general.xml",
                "silva_settings_screen_04_general_bold.xml",
                "silva_settings_screen_05_player.xml",
                "silva_settings_screen_05_player_bold.xml",
                "silva_settings_screen_06_shorts.xml",
                "silva_settings_screen_06_shorts_bold.xml",
                "silva_settings_screen_07_seekbar.xml",
                "silva_settings_screen_07_seekbar_bold.xml",
                "silva_settings_screen_08_swipe_controls.xml",
                "silva_settings_screen_08_swipe_controls_bold.xml",
                "silva_settings_screen_09_return_youtube_dislike.xml",
                "silva_settings_screen_09_return_youtube_dislike_bold.xml",
                "silva_settings_screen_10_sponsorblock.xml",
                "silva_settings_screen_10_sponsorblock_bold.xml",
                "silva_settings_screen_11_misc.xml",
                "silva_settings_screen_11_misc_bold.xml",
                "silva_settings_screen_12_video.xml",
                "silva_settings_screen_12_video_bold.xml",
            )
        )

        // Remove horizontal divider from the settings Preferences
        // To better match the appearance of the stock YouTube settings.
        document("res/values/styles.xml").use { document ->
            val childNodes = document.childNodes

            arrayOf(
                "Theme.YouTube.Settings",
                "Theme.YouTube.Settings.Dark",
            ).forEach { value ->
                val listDividerNode = document.createElement("item")
                listDividerNode.setAttribute("name", "android:listDivider")
                listDividerNode.appendChild(document.createTextNode("@null"))

                childNodes.findElementByAttributeValueOrThrow(
                    "name",
                    value,
                ).appendChild(listDividerNode)
            }
        }

        // Modify the manifest to enhance LicenseActivity behavior
        document("AndroidManifest.xml").use { document ->
            val apiActivity = document.childNodes.findElementByAttributeValueOrThrow(
                "android:name",
                "com.google.android.gms.common.api.GoogleApiActivity",
            )

            // Prevents the activity from being recreated on configuration changes
            // (e.g., screen rotation), preserving its current state and fragment.
            apiActivity.setAttribute(
                "android:configChanges",
                "orientation|screenSize|keyboardHidden"
            )

            // Use same theme as other activities. The exiting theme causes the Morphe
            // settings to fade in and not use a transition animation. A custom theme
            // could be used to not show any transition to match the original YT settings
            // submenu behavior.
            apiActivity.setAttribute(
                "android:theme",
                "@style/Theme.AppCompat.DayNight.NoActionBar"
            )

            val mimeType = document.createElement("data")
            mimeType.setAttribute("android:mimeType", "text/plain")

            // Add a data intent filter with MIME type "text/plain".
            // Some devices crash if undeclared data is passed to an intent,
            // and this change appears to fix the issue.
            val intentFilter = document.createElement("intent-filter")
            intentFilter.appendChild(mimeType)

            apiActivity.appendChild(intentFilter)
        }
    }
}

val settingsPatch = bytecodePatch(
    description = "Adds settings for Morphe to YouTube.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsResourcePatch,
        addResourcesPatch,
        versionCheckPatch,
        fixPlaybackSpeedWhilePlayingPatch,
        fixLikeButtonPatch,
        fixContentProviderPatch,
        // Currently there is no easy way to make a mandatory patch,
        // so for now this is a dependent of this patch.
        checkEnvironmentPatch,
        addLicensePatch,
        experimentalAppNoticePatch(
            mainActivityFingerprint = YouTubeActivityOnCreateFingerprint,
            recommendedAppVersion = COMPATIBILITY_YOUTUBE.targets!!.first { !it.isExperimental }.version!!
        ),
        initializationPatch(
            mainActivityFingerprint = YouTubeActivityOnCreateFingerprint
        )
    )

    execute {
        setAddResourceLocale(localesYouTube)
        addAppResources("shared-youtube")
        addAppResources("youtube")

        // Add an "About" preference to the top.
        preferences += NonInteractivePreference(
            key = "silva_settings_screen_00_about",
            icon = "@drawable/silva_settings_screen_00_about",
            iconBold = "@drawable/silva_settings_screen_00_about_bold",
            layout = "@layout/preference_with_icon",
            summaryKey = null,
            tag = "app.silva.extension.shared.settings.preference.about.SilvaAboutPreference",
            selectable = true,
        )

        if (is_19_34_or_greater) {
            PreferenceScreen.GENERAL.addPreferences(
                SwitchPreference("silva_restore_old_settings_menus")
            )
        }

        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("silva_settings_search_history"),
        )

        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("silva_show_menu_icons")
        )

        PreferenceScreen.MISC.addPreferences(
            TextPreference(
                key = null,
                titleKey = "silva_pref_import_export_title",
                summaryKey = "silva_pref_import_export_summary",
                inputType = InputType.TEXT_MULTI_LINE,
                tag = "app.silva.extension.shared.settings.preference.ImportExportPreference",
            ),
            ListPreference(
                key = "silva_language",
                tag = "app.silva.extension.shared.settings.preference.SortedListPreference"
            )
        )

        // Update shared dark mode status based on YT theme.
        // This is needed because YT allows forcing light/dark mode
        // which then differs from the system dark mode status.
        SetThemeFingerprint.method.apply {
            findInstructionIndicesReversedOrThrow(Opcode.RETURN_OBJECT).forEach { index ->
                val register = getInstruction<OneRegisterInstruction>(index).registerA
                addInstructionsAtControlFlowLabel(
                    index,
                    "invoke-static { v$register }, ${YOUTUBE_ACTIVITY_HOOK_CLASS_DESCRIPTOR}->updateLightDarkModeStatus(Ljava/lang/Enum;)V",
                )
            }
        }

        // Add setting to force Cairo settings fragment on/off.
        CairoFragmentConfigFingerprint.method.insertLiteralOverride(
            CairoFragmentConfigFingerprint.instructionMatches.first().index,
            "$YOUTUBE_ACTIVITY_HOOK_CLASS_DESCRIPTOR->useCairoSettingsFragment(Z)Z"
        )

        // Bold icon resources are found starting in 20.23, but many YT icons are not bold.
        // 20.31 is the first version that seems to have all the bold icons.
        if (is_20_31_or_greater) {
            BoldIconsFeatureFlagFingerprint.let {
                it.method.insertLiteralOverride(
                    it.instructionMatches.first().index,
                    "$YOUTUBE_ACTIVITY_HOOK_CLASS_DESCRIPTOR->useBoldIcons(Z)Z"
                )
            }
        }

        modifyActivityForSettingsInjection(
            GoogleApiActivityOnCreateFingerprint,
            YOUTUBE_ACTIVITY_HOOK_CLASS_DESCRIPTOR,
            false
        )
    }

    finalize {
        PreferenceScreen.close()
    }
}

/**
 * Modifies the activity to show Morphe settings instead of its original purpose.
 */
context(BytecodePatchContext)
internal fun modifyActivityForSettingsInjection(
    activityOnCreateFingerprint: Fingerprint,
    extensionClassType: String,
    isYouTubeMusic: Boolean
) {
    val activityOnCreateClass = activityOnCreateFingerprint.classDef
    val activityOnCreateMethod = activityOnCreateFingerprint.method

    // Modify Activity and remove all existing layout code.
    // Must modify an existing activity and cannot add a new activity to the manifest,
    // as that fails for root installations.
    activityOnCreateMethod.addInstructions(
        0,
        """
            invoke-super { p0, p1 }, ${activityOnCreateClass.superclass}->onCreate(Landroid/os/Bundle;)V
            invoke-static { p0 }, $extensionClassType->initialize(Landroid/app/Activity;)V
            return-void
        """
    )

    // Remove other methods as they will break as the onCreate method is modified above.
    activityOnCreateClass.apply {
        methods.removeIf { it != activityOnCreateMethod && !MethodUtil.isConstructor(it) }
    }

    // Override base context to allow using Morphe specific settings.
    ImmutableMethod(
        activityOnCreateClass.type,
        "attachBaseContext",
        listOf(ImmutableMethodParameter("Landroid/content/Context;", null, null)),
        "V",
        AccessFlags.PROTECTED.value,
        null,
        null,
        MutableMethodImplementation(3),
    ).toMutable().apply {
        addInstructions(
            """
                invoke-static { p1 }, $BASE_ACTIVITY_HOOK_CLASS_DESCRIPTOR->getAttachBaseContext(Landroid/content/Context;)Landroid/content/Context;
                move-result-object p1
                invoke-super { p0, p1 }, ${activityOnCreateClass.superclass}->attachBaseContext(Landroid/content/Context;)V
                return-void
            """
        )
    }.let(activityOnCreateClass.methods::add)

    // Override finish() to intercept back gesture.
    ImmutableMethod(
        activityOnCreateClass.type,
        if (isYouTubeMusic) "finish" else "onBackPressed",
        emptyList(),
        "V",
        AccessFlags.PUBLIC.value,
        null,
        null,
        MutableMethodImplementation(3),
    ).toMutable().apply {
        // Slightly different hooks are needed, otherwise the back button can behave wrong.
        val extensionMethodName = if (isYouTubeMusic) "handleFinish" else "handleBackPress"
        val invokeFinishOpcode = if (isYouTubeMusic) "invoke-super" else "invoke-virtual"

        addInstructions(
            """
                invoke-static {}, $extensionClassType->$extensionMethodName()Z
                move-result v0
                if-nez v0, :search_handled
                $invokeFinishOpcode { p0 }, Landroid/app/Activity;->finish()V
                :search_handled
                return-void
            """
        )
    }.let(activityOnCreateClass.methods::add)
}

/**
 * Creates an intent to open Morphe settings.
 */
fun newIntent(settingsName: String) = IntentPreference.Intent(
    data = settingsName,
    targetClass = "com.google.android.gms.common.api.GoogleApiActivity",
) {
    // The package name change has to be reflected in the intent.
    setOrGetFallbackPackageName("com.google.android.youtube")
}

object PreferenceScreen : BasePreferenceScreen() {
    // Sort screens in the root menu by key, to not scatter related items apart
    // (sorting key is set in silva_prefs.xml).
    // If no preferences are added to a screen, the screen will not be added to the settings.
    val ADS = Screen(
        key = "silva_settings_screen_01_ads",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_01_ads",
        iconBold = "@drawable/silva_settings_screen_01_ads_bold",
        layout = "@layout/preference_with_icon",
    )
    val ALTERNATIVE_THUMBNAILS = Screen(
        key = "silva_settings_screen_02_alt_thumbnails",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_02_alt_thumbnails",
        iconBold = "@drawable/silva_settings_screen_02_alt_thumbnails_bold",
        layout = "@layout/preference_with_icon",
        sorting = Sorting.UNSORTED,
    )
    val FEED = Screen(
        key = "silva_settings_screen_03_feed",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_03_feed",
        iconBold = "@drawable/silva_settings_screen_03_feed_bold",
        layout = "@layout/preference_with_icon",
    )
    val GENERAL = Screen(
        key = "silva_settings_screen_04_general",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_04_general",
        iconBold = "@drawable/silva_settings_screen_04_general_bold",
        layout = "@layout/preference_with_icon",
    )
    val PLAYER = Screen(
        key = "silva_settings_screen_05_player",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_05_player",
        iconBold = "@drawable/silva_settings_screen_05_player_bold",
        layout = "@layout/preference_with_icon",
    )
    val SHORTS = Screen(
        key = "silva_settings_screen_06_shorts",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_06_shorts",
        iconBold = "@drawable/silva_settings_screen_06_shorts_bold",
        layout = "@layout/preference_with_icon",
    )
    val SEEKBAR = Screen(
        key = "silva_settings_screen_07_seekbar",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_07_seekbar",
        iconBold = "@drawable/silva_settings_screen_07_seekbar_bold",
        layout = "@layout/preference_with_icon",
    )
    val SWIPE_CONTROLS = Screen(
        key = "silva_settings_screen_08_swipe_controls",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_08_swipe_controls",
        iconBold = "@drawable/silva_settings_screen_08_swipe_controls_bold",
        layout = "@layout/preference_with_icon",
        sorting = Sorting.UNSORTED,
    )
    val RETURN_YOUTUBE_DISLIKE = Screen(
        key = "silva_settings_screen_09_return_youtube_dislike",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_09_return_youtube_dislike",
        iconBold = "@drawable/silva_settings_screen_09_return_youtube_dislike_bold",
        layout = "@layout/preference_with_icon",
        sorting = Sorting.UNSORTED,
    )
    val SPONSORBLOCK = Screen(
        key = "silva_settings_screen_10_sponsorblock",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_10_sponsorblock",
        iconBold = "@drawable/silva_settings_screen_10_sponsorblock_bold",
        layout = "@layout/preference_with_icon",
        sorting = Sorting.UNSORTED,
    )
    val MISC = Screen(
        key = "silva_settings_screen_11_misc",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_11_misc",
        iconBold = "@drawable/silva_settings_screen_11_misc_bold",
        layout = "@layout/preference_with_icon",
    )
    val VIDEO = Screen(
        key = "silva_settings_screen_12_video",
        summaryKey = null,
        icon = "@drawable/silva_settings_screen_12_video",
        iconBold = "@drawable/silva_settings_screen_12_video_bold",
        layout = "@layout/preference_with_icon",
        sorting = Sorting.BY_KEY,
    )

    override fun commit(screen: PreferenceScreenPreference) {
        preferences += screen
    }
}
