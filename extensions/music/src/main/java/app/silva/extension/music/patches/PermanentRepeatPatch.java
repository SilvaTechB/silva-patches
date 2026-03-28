package app.silva.extension.music.patches;

import app.silva.extension.music.settings.Settings;

@SuppressWarnings("unused")
public class PermanentRepeatPatch {

    /**
     * Injection point
     */
    public static boolean permanentRepeat() {
        return Settings.PERMANENT_REPEAT.get();
    }
}
