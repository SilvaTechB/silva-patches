package app.silva.extension.youtube.patches;

import app.silva.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class HideRelatedVideoOverlayPatch {
    /**
     * Injection point.
     */
    public static boolean hideRelatedVideoOverlay() {
        return Settings.HIDE_PLAYER_RELATED_VIDEOS_OVERLAY.get();
    }
}
