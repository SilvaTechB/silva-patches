/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.extension.reddit.patches;

import app.silva.extension.reddit.settings.Settings;

@SuppressWarnings("unused")
public final class SanitizeSharingLinksPatch {

    /**
     * @return If this patch was included during patching.
     */
    public static boolean isPatchIncluded() {
        return false;  // Modified during patching.
    }

    /**
     * Injection point.
     */
    public static boolean stripQueryParameters() {
        return Settings.SANITIZE_SHARING_LINKS.get();
    }
}
