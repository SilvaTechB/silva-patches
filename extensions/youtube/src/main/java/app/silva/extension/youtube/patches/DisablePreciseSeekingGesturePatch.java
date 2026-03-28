package app.silva.extension.youtube.patches;

import app.silva.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class DisablePreciseSeekingGesturePatch {
    public static boolean isGestureDisabled() {
        return Settings.DISABLE_PRECISE_SEEKING_GESTURE.get();
    }
}
