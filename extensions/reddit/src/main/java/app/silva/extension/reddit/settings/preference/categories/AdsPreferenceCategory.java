/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.extension.reddit.settings.preference.categories;

import static app.silva.extension.shared.StringRef.str;

import android.content.Context;
import android.preference.PreferenceScreen;

import app.silva.extension.reddit.patches.HideAdsPatch;
import app.silva.extension.reddit.settings.Settings;
import app.silva.extension.reddit.settings.preference.BooleanSettingPreference;

@SuppressWarnings("deprecation")
public class AdsPreferenceCategory extends ConditionalPreferenceCategory {
    public AdsPreferenceCategory(Context context, PreferenceScreen screen) {
        super(context, screen);
        setTitle(str("silva_screen_layout_title"));
    }

    @Override
    public boolean getSettingsStatus() {
        return HideAdsPatch.isPatchIncluded();
    }

    @Override
    public void addPreferences(Context context) {
        addPreference(new BooleanSettingPreference(
                context,
                Settings.HIDE_COMMENT_ADS
        ));
        addPreference(new BooleanSettingPreference(
                context,
                Settings.HIDE_POST_ADS
        ));
    }
}
