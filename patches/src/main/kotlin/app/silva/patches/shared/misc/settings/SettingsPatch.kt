package app.silva.patches.shared.misc.settings

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.silva.patches.all.misc.resources.addAppResources
import app.silva.patches.all.misc.resources.addResourcesPatch
import app.silva.patches.shared.layout.branding.addLicensePatch
import app.silva.patches.shared.misc.extension.EXTENSION_CLASS_DESCRIPTOR
import app.silva.patches.shared.misc.settings.preference.BasePreference
import app.silva.patches.shared.misc.settings.preference.PreferenceCategory
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.silva.util.ResourceGroup
import app.silva.util.copyResources
import app.silva.util.getNode
import app.silva.util.insertFirst
import app.silva.util.returnEarly
import org.w3c.dom.Node

const val SILVA_SETTINGS_INTENT = "silva_settings_intent"

private var lightThemeColor : String? = null
private var darkThemeColor : String? = null

/**
 * Sets the default theme colors used in various Morphe specific settings menus.
 * By default, these colors are white and black, but instead can be set to the
 * same color the target app uses for its own settings.
 */
fun overrideThemeColors(lightThemeColorString: String?, darkThemeColorString: String) {
    lightThemeColor = lightThemeColorString
    darkThemeColor = darkThemeColorString
}

private val settingsColorPatch = bytecodePatch {
    finalize {
        if (lightThemeColor != null) {
            ThemeLightColorResourceNameFingerprint.method.returnEarly(lightThemeColor!!)
        }
        if (darkThemeColor != null) {
            ThemeDarkColorResourceNameFingerprint.method.returnEarly(darkThemeColor!!)
        }
    }
}

/**
 * A resource patch that adds settings to a settings fragment.
 *
 * @param rootPreferences List of intent preferences and the name of the fragment file to add it to.
 *                        File names that do not exist are ignored and not processed.
 * @param preferences A set of preferences to add to the Morphe fragment.
 */
fun settingsPatch (
    rootPreferences: List<Pair<BasePreference, String>>? = null,
    preferences: Set<BasePreference>,
) = resourcePatch {
    dependsOn(
        addResourcesPatch,
        settingsColorPatch,
        addLicensePatch
    )

    execute {
        addAppResources("shared")

        copyResources(
            "settings",
            ResourceGroup("xml",
                "silva_prefs.xml",
                "silva_prefs_icons.xml",
                "silva_prefs_icons_bold.xml"
            ),
            ResourceGroup("menu",
                "silva_search_menu.xml"
            ),
            ResourceGroup("drawable",
                // CustomListPreference resources.
                "silva_ic_dialog_alert.xml",
                // Search resources.
                "silva_settings_arrow_time.xml",
                "silva_settings_arrow_time_bold.xml",
                "silva_settings_custom_checkmark.xml",
                "silva_settings_custom_checkmark_bold.xml",
                "silva_settings_search_icon.xml",
                "silva_settings_search_icon_bold.xml",
                "silva_settings_search_remove.xml",
                "silva_settings_search_remove_bold.xml",
                "silva_settings_toolbar_arrow_left.xml",
                "silva_settings_toolbar_arrow_left_bold.xml",
            ),
            ResourceGroup("layout",
                "silva_custom_list_item_checked.xml",
                // Color picker.
                "silva_color_dot_widget.xml",
                "silva_color_picker.xml",
                // Search.
                "silva_preference_search_history_item.xml",
                "silva_preference_search_history_screen.xml",
                "silva_preference_search_no_result.xml",
                "silva_preference_search_result_color.xml",
                "silva_preference_search_result_group_header.xml",
                "silva_preference_search_result_list.xml",
                "silva_preference_search_result_regular.xml",
                "silva_preference_search_result_switch.xml",
                "silva_settings_with_toolbar.xml"
            )
        )
    }

    finalize {
        fun Node.addPreference(preference: BasePreference) {
            preference.serialize(ownerDocument) { resource ->
                // FIXME? Not needed anymore?
//                addResource("values", resource)
            }.let { preferenceNode ->
                insertFirst(preferenceNode)
            }
        }

        // Add the root preference to an existing fragment if needed.
        rootPreferences?.let {
            var modified = false

            it.forEach { (intent, fileName) ->
                val preferenceFileName = "res/xml/$fileName.xml"
                if (get(preferenceFileName).exists()) {
                    document(preferenceFileName).use { document ->
                        document.getNode("PreferenceScreen").addPreference(intent)
                    }
                    modified = true
                }
            }

            if (!modified) throw PatchException("No declared preference files exists: $rootPreferences")
        }

        // Add all preferences to the Morphe fragment.
        document("res/xml/silva_prefs_icons.xml").use { document ->
            val morphePreferenceScreenNode = document.getNode("PreferenceScreen")
            preferences.forEach { morphePreferenceScreenNode.addPreference(it) }
        }

        // Because the icon preferences require declaring a layout resource,
        // there is no easy way to change to the Android default preference layout
        // after the preference is inflated.
        // Using two different preference files is the simplest and most robust solution.
        fun removeIconsAndLayout(preferences: Collection<BasePreference>, removeAllIconsAndLayout: Boolean) {
            preferences.forEach { preference ->
                preference.icon = null
                if (removeAllIconsAndLayout) {
                    preference.iconBold = null
                    preference.layout = null
                }

                if (preference is PreferenceCategory) {
                    removeIconsAndLayout(preference.preferences, removeAllIconsAndLayout)
                } else if (preference is PreferenceScreenPreference) {
                    removeIconsAndLayout(preference.preferences, removeAllIconsAndLayout)
                }
            }
        }

        // Bold icons.
        removeIconsAndLayout(preferences, false)
        document("res/xml/silva_prefs_icons_bold.xml").use { document ->
            val morphePreferenceScreenNode = document.getNode("PreferenceScreen")
            preferences.forEach { morphePreferenceScreenNode.addPreference(it) }
        }

        removeIconsAndLayout(preferences, true)

        document("res/xml/silva_prefs.xml").use { document ->
            val morphePreferenceScreenNode = document.getNode("PreferenceScreen")
            preferences.forEach { morphePreferenceScreenNode.addPreference(it) }
        }
    }
}
