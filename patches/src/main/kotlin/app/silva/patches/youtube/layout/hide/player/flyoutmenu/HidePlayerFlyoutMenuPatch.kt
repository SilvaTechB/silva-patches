package app.silva.patches.youtube.layout.hide.player.flyoutmenu

import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.litho.filter.addLithoFilter
import app.silva.patches.youtube.misc.litho.filter.lithoFilterPatch
import app.silva.patches.youtube.misc.playertype.playerTypeHookPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

@Suppress("unused")
val hidePlayerFlyoutMenuPatch = bytecodePatch(
    name = "Hide player flyout menu items",
    description = "Adds options to hide menu items that appear when pressing the gear icon in the video player.",
) {
    dependsOn(
        lithoFilterPatch,
        playerTypeHookPatch,
        settingsPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        val filterClassDescriptor = "Lapp/morphe/extension/youtube/patches/components/PlayerFlyoutMenuItemsFilter;"

        PreferenceScreen.PLAYER.addPreferences(
            PreferenceScreenPreference(
                key = "silva_hide_player_flyout",
                preferences = setOf(
                    SwitchPreference("silva_hide_player_flyout_captions"),
                    SwitchPreference("silva_hide_player_flyout_listen_with_youtube_music"),
                    SwitchPreference("silva_hide_player_flyout_help"),
                    SwitchPreference("silva_hide_player_flyout_speed"),
                    SwitchPreference("silva_hide_player_flyout_lock_screen"),
                    SwitchPreference(
                        key = "silva_hide_player_flyout_audio_track",
                        tag = "app.silva.extension.youtube.settings.preference.HideAudioFlyoutMenuPreference"
                    ),
                    SwitchPreference("silva_hide_player_flyout_video_quality"),
                    SwitchPreference("silva_hide_player_flyout_video_quality_footer"),
                    SwitchPreference("silva_hide_player_flyout_additional_settings"),
                    SwitchPreference("silva_hide_player_flyout_ambient_mode"),
                    SwitchPreference("silva_hide_player_flyout_stable_volume"),
                    SwitchPreference("silva_hide_player_flyout_loop_video"),
                    SwitchPreference("silva_hide_player_flyout_sleep_timer"),
                    SwitchPreference("silva_hide_player_flyout_watch_in_vr"),
                )
            )
        )

        addLithoFilter(filterClassDescriptor)
    }
}
