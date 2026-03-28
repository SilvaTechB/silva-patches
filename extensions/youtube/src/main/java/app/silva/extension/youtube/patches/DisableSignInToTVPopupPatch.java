package app.silva.extension.youtube.patches;

import app.silva.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class DisableSignInToTVPopupPatch {

    /**
     * Injection point.
     */
    public static boolean disableSignInToTVPopup() {
        return Settings.DISABLE_SIGN_IN_TO_TV_POPUP.get();
    }
}
