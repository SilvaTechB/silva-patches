package app.silva.patches.music.misc.spoof

import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.playservice.is_7_16_or_greater
import app.silva.patches.music.misc.playservice.is_7_33_or_greater
import app.silva.patches.music.misc.playservice.is_8_11_or_greater
import app.silva.patches.music.misc.playservice.is_8_15_or_greater
import app.silva.patches.music.misc.playservice.is_8_40_or_greater
import app.silva.patches.music.misc.playservice.versionCheckPatch
import app.silva.patches.music.misc.settings.PreferenceScreen
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.music.shared.MusicActivityOnCreateFingerprint
import app.silva.patches.shared.misc.settings.preference.ListPreference
import app.silva.patches.shared.misc.settings.preference.NonInteractivePreference
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.shared.misc.settings.preference.TextPreference
import app.silva.patches.shared.misc.spoof.spoofVideoStreamsPatch

val spoofVideoStreamsPatch = spoofVideoStreamsPatch(
    extensionClassDescriptor = "Lapp/silva/extension/music/patches/spoof/SpoofVideoStreamsPatch;",
    mainActivityOnCreateFingerprint = MusicActivityOnCreateFingerprint,
    fixMediaFetchHotConfig = { is_7_16_or_greater },
    fixMediaFetchHotConfigAlternative = { is_8_11_or_greater && !is_8_15_or_greater },
    fixParsePlaybackResponseFeatureFlag = { is_7_33_or_greater },
    fixMediaSessionFeatureFlag = { is_8_40_or_greater },

    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch,
            userAgentClientSpoofPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },

    executeBlock = {

        PreferenceScreen.MISC.addPreferences(
            PreferenceScreenPreference(
                key = "silva_spoof_video_streams_screen",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("silva_spoof_video_streams"),
                    ListPreference("silva_spoof_video_streams_client_type"),
                    NonInteractivePreference(
                        key = "silva_spoof_video_streams_sign_in_android_vr_about",
                        tag = "app.silva.extension.music.settings.preference.SpoofVideoStreamsSignInPreference",
                        selectable = true,
                    ),
                    SwitchPreference("silva_spoof_video_streams_disable_player_js_update"),
                    TextPreference("silva_spoof_video_streams_player_js_hash_value"),
                )
            )
        )
    }
)
