package app.silva.extension.music.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.silva.extension.shared.settings.Setting.parent;

import app.silva.extension.music.patches.ChangeHeaderPatch.HeaderLogo;
import app.silva.extension.music.patches.ChangeStartPagePatch.StartPage;
import app.silva.extension.shared.Logger;
import app.silva.extension.shared.settings.BooleanSetting;
import app.silva.extension.shared.settings.EnumSetting;
import app.silva.extension.shared.settings.SharedYouTubeSettings;
import app.silva.extension.shared.spoof.ClientType;

public class Settings extends SharedYouTubeSettings {

    // Ads
    public static final BooleanSetting HIDE_VIDEO_ADS = new BooleanSetting("silva_music_hide_video_ads", TRUE, true);
    public static final BooleanSetting HIDE_GET_PREMIUM_LABEL = new BooleanSetting("silva_music_hide_get_premium_label", TRUE, true);

    // General (Layout)
    public static final EnumSetting<StartPage> CHANGE_START_PAGE = new EnumSetting<>("silva_change_start_page", StartPage.DEFAULT, true);
    public static final BooleanSetting HIDE_CAST_BUTTON = new BooleanSetting("silva_music_hide_cast_button", TRUE, true);
    public static final BooleanSetting HIDE_CATEGORY_BAR = new BooleanSetting("silva_music_hide_category_bar", FALSE, true);
    public static final BooleanSetting HIDE_HISTORY_BUTTON = new BooleanSetting("silva_music_hide_history_button", FALSE, true);
    public static final BooleanSetting HIDE_SEARCH_BUTTON = new BooleanSetting("silva_music_hide_search_button", FALSE, true);
    public static final BooleanSetting HIDE_NOTIFICATION_BUTTON = new BooleanSetting("silva_music_hide_notification_button", FALSE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BAR_HOME_BUTTON = new BooleanSetting("silva_music_hide_navigation_bar_home_button", FALSE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BAR_SAMPLES_BUTTON = new BooleanSetting("silva_music_hide_navigation_bar_samples_button", FALSE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BAR_EXPLORE_BUTTON = new BooleanSetting("silva_music_hide_navigation_bar_explore_button", FALSE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BAR_LIBRARY_BUTTON = new BooleanSetting("silva_music_hide_navigation_bar_library_button", FALSE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BAR_UPGRADE_BUTTON = new BooleanSetting("silva_music_hide_navigation_bar_upgrade_button", TRUE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BAR = new BooleanSetting("silva_music_hide_navigation_bar", FALSE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BAR_LABEL = new BooleanSetting("silva_music_hide_navigation_bar_labels", FALSE, true);
    public static final EnumSetting<HeaderLogo> HEADER_LOGO = new EnumSetting<>("silva_header_logo", HeaderLogo.DEFAULT, true);

    // Player
    public static final BooleanSetting MINIPLAYER_NEXT_BUTTON = new BooleanSetting("silva_music_miniplayer_next_button", TRUE, true);
    public static final BooleanSetting MINIPLAYER_PREVIOUS_BUTTON = new BooleanSetting("silva_music_miniplayer_previous_button", TRUE, true);
    public static final BooleanSetting CHANGE_MINIPLAYER_COLOR = new BooleanSetting("silva_music_change_miniplayer_color", FALSE, true);
    public static final BooleanSetting ENABLE_FORCED_MINIPLAYER = new BooleanSetting("silva_music_enable_forced_miniplayer", FALSE, true);
    public static final BooleanSetting PERMANENT_REPEAT = new BooleanSetting("silva_music_play_permanent_repeat", FALSE, true);

    // Miscellaneous
    public static final EnumSetting<ClientType> SPOOF_VIDEO_STREAMS_CLIENT_TYPE = new EnumSetting<>("silva_spoof_video_streams_client_type",
            ClientType.ANDROID_REEL, true, parent(SPOOF_VIDEO_STREAMS));

    public static final BooleanSetting FORCE_ORIGINAL_AUDIO = new BooleanSetting("silva_force_original_audio", TRUE, true);

    static {
        // region Migration

        // TV Simply may require PoToken
        if (SPOOF_VIDEO_STREAMS_CLIENT_TYPE.get() == ClientType.TV_SIMPLY) {
            Logger.printInfo(() -> "Migrating from TV Simply to TV");
            SPOOF_VIDEO_STREAMS_CLIENT_TYPE.save(ClientType.TV);
        }

        // endregion
    }
}
