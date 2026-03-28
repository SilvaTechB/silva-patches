package app.silva.patches.youtube.interaction.downloads

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.shared.misc.settings.preference.TextPreference
import app.silva.patches.youtube.misc.playercontrols.addBottomControl
import app.silva.patches.youtube.misc.playercontrols.initializeBottomControl
import app.silva.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.silva.patches.youtube.misc.playercontrols.playerControlsPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint
import app.silva.patches.youtube.video.information.videoInformationPatch
import app.silva.util.ResourceGroup
import app.silva.util.copyResources

private val downloadsResourcePatch = resourcePatch {
    dependsOn(
        playerControlsPatch,
        settingsPatch,
    )

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            PreferenceScreenPreference(
                key = "silva_external_downloader_screen",
                sorting = Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("silva_external_downloader"),
                    SwitchPreference("silva_external_downloader_action_button"),
                    TextPreference(
                        "silva_external_downloader_name",
                        tag = "app.silva.extension.youtube.settings.preference.ExternalDownloaderPreference",
                    ),
                ),
            ),
        )

        copyResources(
            "downloads",
            ResourceGroup("drawable", "silva_yt_download_button.xml"),
        )

        addBottomControl("downloads")
    }
}

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/silva/extension/youtube/patches/DownloadsPatch;"

internal const val BUTTON_DESCRIPTOR = "Lapp/silva/extension/youtube/videoplayer/ExternalDownloadButton;"

@Suppress("unused")
val downloadsPatch = bytecodePatch(
    name = "Downloads",
    description = "Adds support to download videos with an external downloader app " +
        "using the in-app download button or a video player action button.",
) {
    dependsOn(
        downloadsResourcePatch,
        playerControlsPatch,
        videoInformationPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        initializeBottomControl(BUTTON_DESCRIPTOR)
        injectVisibilityCheckCall(BUTTON_DESCRIPTOR)

        // Main activity is used to launch downloader intent.
        YouTubeActivityOnCreateFingerprint.method.addInstruction(
            0,
            "invoke-static/range { p0 .. p0 }, ${EXTENSION_CLASS_DESCRIPTOR}->setMainActivity(Landroid/app/Activity;)V"
        )

        OfflineVideoEndpointFingerprint.method.apply {
            addInstructionsWithLabels(
                0,
                """
                    invoke-static/range {p3 .. p3}, $EXTENSION_CLASS_DESCRIPTOR->inAppDownloadButtonOnClick(Ljava/lang/String;)Z
                    move-result v0
                    if-eqz v0, :show_native_downloader
                    return-void
                    :show_native_downloader
                    nop
                """,
            )
        }
    }
}
