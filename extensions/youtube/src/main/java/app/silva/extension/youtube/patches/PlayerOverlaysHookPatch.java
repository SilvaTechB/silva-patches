package app.silva.extension.youtube.patches;

import android.view.ViewGroup;

import app.silva.extension.youtube.shared.PlayerOverlays;

@SuppressWarnings("unused")
public class PlayerOverlaysHookPatch {
    /**
     * Injection point.
     */
    public static void playerOverlayInflated(ViewGroup group) {
        PlayerOverlays.attach(group);
    }
}