package app.silva.extension.youtube.patches;

import app.silva.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class DisablePlayerPopupPanelsPatch {
    /**
     * Injection point.
     */
    public static boolean disablePlayerPopupPanels() {
        return Settings.DISABLE_PLAYER_POPUP_PANELS.get();
    }
}
