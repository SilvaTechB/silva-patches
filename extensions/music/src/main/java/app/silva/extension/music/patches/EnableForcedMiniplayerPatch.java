/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.extension.music.patches;

import app.silva.extension.music.settings.Settings;

@SuppressWarnings("unused")
public class EnableForcedMiniplayerPatch {

    /**
     * Injection point
     */
    public static boolean enableForcedMiniplayerPatch(boolean original) {
        return Settings.ENABLE_FORCED_MINIPLAYER.get() || original;
    }
}