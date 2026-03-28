/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.extension.reddit.patches;

import app.silva.extension.reddit.settings.Settings;

@SuppressWarnings("unused")
public final class ShowViewCountPatch {
    private static final String ANDROID_POST_UNIT_VIEWS_COUNT = "android_post_unit_views_count_v2";

    /**
     * @return If this patch was included during patching.
     */
    public static boolean isPatchIncluded() {
        return false;  // Modified during patching.
    }

    /**
     * Injection point.
     */
    public static boolean showViewCount(String experimentName, boolean original) {
        if (Settings.SHOW_VIEW_COUNT.get() && experimentName != null && experimentName.startsWith(ANDROID_POST_UNIT_VIEWS_COUNT)) {
            return true;
        }

        return original;
    }
}
