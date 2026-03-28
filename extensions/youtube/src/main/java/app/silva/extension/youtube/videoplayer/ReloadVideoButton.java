package app.silva.extension.youtube.videoplayer;

import android.view.View;

import androidx.annotation.Nullable;

import app.silva.extension.shared.Logger;
import app.silva.extension.youtube.patches.ReloadVideoPatch;
import app.silva.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class ReloadVideoButton {
    @Nullable
    private static PlayerControlButton instance;

    /**
     * injection point.
     */
    public static void initializeButton(View controlsView) {
        try {
            instance = new PlayerControlButton(
                    controlsView,
                    "silva_reload_video_button",
                    null,
                    Settings.RELOAD_VIDEO::get,
                    v -> ReloadVideoPatch.reloadVideo(),
                    null
            );
        } catch (Exception ex) {
            Logger.printException(() -> "initialize failure", ex);
        }
    }

    /**
     * injection point.
     */
    public static void setVisibilityNegatedImmediate() {
        if (instance != null) instance.setVisibilityNegatedImmediate();
    }

    /**
     * injection point.
     */
    public static void setVisibilityImmediate(boolean visible) {
        if (instance != null) instance.setVisibilityImmediate(visible);
    }

    /**
     * injection point.
     */
    public static void setVisibility(boolean visible, boolean animated) {
        if (instance != null) instance.setVisibility(visible, animated);
    }
}
