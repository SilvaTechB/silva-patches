/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.extension.reddit.settings.preference;

import static app.silva.extension.shared.StringRef.str;

import android.content.Context;
import android.preference.SwitchPreference;

import app.silva.extension.shared.settings.BooleanSetting;

@SuppressWarnings("deprecation")
public class BooleanSettingPreference extends SwitchPreference {

    public BooleanSettingPreference(Context context, BooleanSetting setting) {
        super(context);
        this.setTitle(str(setting.key + "_title"));
        this.setSummary(str(setting.key + "_summary"));
        this.setKey(setting.key);
        this.setChecked(setting.get());
    }
}
