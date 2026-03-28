package app.silva.extension.music.patches;

import static app.silva.extension.shared.Utils.hideViewBy0dpUnderCondition;

import android.view.View;

import app.silva.extension.music.settings.Settings;

@SuppressWarnings("unused")
public class HideCategoryBarPatch {

    /**
     * Injection point
     */
    public static void hideCategoryBar(View view) {
        hideViewBy0dpUnderCondition(Settings.HIDE_CATEGORY_BAR, view);
    }
}
