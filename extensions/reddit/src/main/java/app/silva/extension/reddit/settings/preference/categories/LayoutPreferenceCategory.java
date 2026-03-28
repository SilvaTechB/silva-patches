/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.extension.reddit.settings.preference.categories;

import static app.silva.extension.reddit.patches.VersionCheckPatch.is_2025_52_or_greater;
import static app.silva.extension.shared.StringRef.str;

import android.content.Context;
import android.preference.PreferenceScreen;

import app.silva.extension.reddit.patches.DisableModernHomePatch;
import app.silva.extension.reddit.patches.DisableScreenshotPopupPatch;
import app.silva.extension.reddit.patches.HideNavigationButtonsPatch;
import app.silva.extension.reddit.patches.HideRecommendedCommunitiesShelf;
import app.silva.extension.reddit.patches.HideSidebarComponentsPatch;
import app.silva.extension.reddit.patches.HideTrendingTodayShelfPatch;
import app.silva.extension.reddit.patches.RemoveSubRedditDialogPatch;
import app.silva.extension.reddit.patches.ShowViewCountPatch;
import app.silva.extension.reddit.settings.Settings;
import app.silva.extension.reddit.settings.preference.BooleanSettingPreference;

@SuppressWarnings("deprecation")
public class LayoutPreferenceCategory extends ConditionalPreferenceCategory {
    public LayoutPreferenceCategory(Context context, PreferenceScreen screen) {
        super(context, screen);
        setTitle(str("silva_screen_layout_title"));
    }

    @Override
    public boolean getSettingsStatus() {
        return DisableScreenshotPopupPatch.isPatchIncluded() ||
                HideNavigationButtonsPatch.isPatchIncluded() ||
                HideSidebarComponentsPatch.isPatchIncluded() ||
                HideRecommendedCommunitiesShelf.isPatchIncluded() ||
                HideTrendingTodayShelfPatch.isPatchIncluded() ||
                RemoveSubRedditDialogPatch.isPatchIncluded();
    }

    @Override
    public void addPreferences(Context context) {
        if (DisableModernHomePatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.DISABLE_MODERN_HOME
            ));
        }

        if (DisableScreenshotPopupPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.DISABLE_SCREENSHOT_POPUP
            ));
        }

        if (HideNavigationButtonsPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_ANSWERS_BUTTON
            ));
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_CHAT_BUTTON
            ));
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_CREATE_BUTTON
            ));
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_DISCOVER_BUTTON
            ));
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_GAMES_BUTTON
            ));
        }

        if (HideSidebarComponentsPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_RECENTLY_VISITED_SHELF
            ));
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_GAMES_ON_REDDIT_SHELF
            ));
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_REDDIT_PRO_SHELF
            ));

            if (is_2025_52_or_greater) {
                addPreference(new BooleanSettingPreference(
                        context,
                        Settings.HIDE_ABOUT_SHELF
                ));
                addPreference(new BooleanSettingPreference(
                        context,
                        Settings.HIDE_RESOURCES_SHELF
                ));
            }
        }

        if (HideRecommendedCommunitiesShelf.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_RECOMMENDED_COMMUNITIES_SHELF
            ));
        }

        if (HideTrendingTodayShelfPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.HIDE_TRENDING_TODAY_SHELF
            ));
        }

        if (RemoveSubRedditDialogPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.REMOVE_NSFW_DIALOG
            ));
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.REMOVE_NOTIFICATION_DIALOG
            ));
        }

        if (ShowViewCountPatch.isPatchIncluded()) {
            addPreference(new BooleanSettingPreference(
                    context,
                    Settings.SHOW_VIEW_COUNT
            ));
        }
    }
}
