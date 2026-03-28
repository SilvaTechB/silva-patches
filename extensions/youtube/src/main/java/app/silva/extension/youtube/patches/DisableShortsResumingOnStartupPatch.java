package app.silva.extension.youtube.patches;

import app.silva.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class DisableShortsResumingOnStartupPatch {

    /**
     * Injection point.
     */
    public static boolean disableShortsResumingOnStartup() {
        return Settings.DISABLE_SHORTS_RESUMING_ON_STARTUP.get();
    }

    /**
     * Injection point.
     */
    public static boolean disableShortsResumingOnStartup(boolean original) {
        return original && !Settings.DISABLE_SHORTS_RESUMING_ON_STARTUP.get();
    }
}