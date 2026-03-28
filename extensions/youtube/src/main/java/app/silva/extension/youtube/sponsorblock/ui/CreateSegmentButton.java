package app.silva.extension.youtube.sponsorblock.ui;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import app.silva.extension.shared.Logger;
import app.silva.extension.shared.ResourceType;
import app.silva.extension.shared.ResourceUtils;
import app.silva.extension.shared.Utils;
import app.silva.extension.youtube.settings.Settings;
import app.silva.extension.youtube.sponsorblock.SegmentPlaybackController;
import app.silva.extension.youtube.videoplayer.PlayerControlButton;

@SuppressWarnings("unused")
public class CreateSegmentButton {

    private static final int DRAWABLE_SB_LOGO = ResourceUtils.getIdentifierOrThrow(
            ResourceType.DRAWABLE, Utils.appIsUsingBoldIcons()
                    ? "silva_sb_logo_bold"
                    : "silva_sb_logo"
    );

    @Nullable
    private static PlayerControlButton instance;

    public static void hideControls() {
        if (instance != null) instance.hide();
    }

    /**
     * injection point.
     */
    public static void initializeButton(View controlsView) {
        try {
            instance = new PlayerControlButton(
                    controlsView,
                    "silva_sb_create_segment_button",
                    null,
                    CreateSegmentButton::isButtonEnabled,
                    v -> SponsorBlockViewController.toggleNewSegmentLayoutVisibility(),
                    null
            );

            if (false) {
                // FIXME: Bold YT player icons are currently forced off.
                //        Enable this logic when the new player icons are not forced off.
                ImageView icon = Utils.getChildViewByResourceName(controlsView,
                        "silva_sb_create_segment_button");
                icon.setImageResource(DRAWABLE_SB_LOGO);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "initializeButton failure", ex);
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

    private static boolean isButtonEnabled() {
        return Settings.SB_ENABLED.get() && Settings.SB_CREATE_NEW_SEGMENT.get()
                && !SegmentPlaybackController.isAdProgressTextVisible();
    }
}
