/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.extension.reddit.settings.preference.categories;

import static app.silva.extension.shared.StringRef.str;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import app.silva.extension.reddit.patches.OpenLinksDirectlyPatch;
import app.silva.extension.reddit.patches.SanitizeSharingLinksPatch;
import app.silva.extension.reddit.settings.Settings;
import app.silva.extension.reddit.settings.preference.BooleanSettingPreference;
import app.silva.extension.shared.settings.BaseSettings;
import app.silva.extension.shared.settings.preference.ImportExportPreference;
import app.silva.extension.shared.settings.preference.about.SilvaAboutPreference;
import app.silva.extension.shared.settings.preference.SortedListPreference;

@SuppressWarnings("deprecation")
public class MiscellaneousPreferenceCategory extends ConditionalPreferenceCategory {
    public MiscellaneousPreferenceCategory(Context context, PreferenceScreen screen) {
        super(context, screen);
        setTitle(str("silva_screen_miscellaneous_title"));
    }

    @Override
    public boolean getSettingsStatus() {
        return OpenLinksDirectlyPatch.isPatchIncluded() ||
                SanitizeSharingLinksPatch.isPatchIncluded();
    }

    @Override
    public void addPreferences(Context context) {
        SilvaAboutPreference.showVancedAsPastContributor(false);
        Preference about = new SilvaAboutPreference(context);
        about.setTitle(str("silva_about_title"));
        about.setSummary(str("silva_about_summary"));
        addPreference(about);

        ImportExportPreference importPref = new ImportExportPreference(context);
        importPref.setTitle(str("silva_pref_import_export_title"));
        importPref.setSummary(str("silva_pref_import_export_summary"));
        addPreference(importPref);

        SortedListPreference language = new SortedListPreference(context, BaseSettings.SILVA_LANGUAGE);
        addPreference(language);

        if (OpenLinksDirectlyPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.OPEN_LINKS_DIRECTLY
            ));
        }
        if (SanitizeSharingLinksPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.SANITIZE_SHARING_LINKS
            ));
        }
    }
}
