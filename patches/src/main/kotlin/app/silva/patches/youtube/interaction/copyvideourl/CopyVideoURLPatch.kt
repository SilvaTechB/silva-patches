package app.silva.patches.youtube.interaction.copyvideourl

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.playercontrols.addBottomControl
import app.silva.patches.youtube.misc.playercontrols.initializeBottomControl
import app.silva.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.silva.patches.youtube.misc.playercontrols.playerControlsPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.video.information.videoInformationPatch
import app.silva.util.ResourceGroup
import app.silva.util.copyResources

private val copyVideoURLResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        playerControlsPatch,
    )

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            SwitchPreference("silva_copy_video_url"),
            SwitchPreference("silva_copy_video_url_timestamp"),
        )

        copyResources(
            "copyvideourl",
            ResourceGroup(
                resourceDirectoryName = "drawable",
                "silva_yt_copy.xml",
                "silva_yt_copy_timestamp.xml",
            ),
        )

        addBottomControl("copyvideourl")
    }
}

@Suppress("unused")
val copyVideoURLPatch = bytecodePatch(
    name = "Copy video URL",
    description = "Adds options to display buttons in the video player to copy video URLs.",
) {
    dependsOn(
        copyVideoURLResourcePatch,
        playerControlsPatch,
        videoInformationPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        val extensionPlayerPackage = "Lapp/morphe/extension/youtube/videoplayer"
        val buttonsDescriptors = listOf(
            "$extensionPlayerPackage/CopyVideoURLButton;",
            "$extensionPlayerPackage/CopyVideoURLTimestampButton;",
        )

        buttonsDescriptors.forEach { descriptor ->
            initializeBottomControl(descriptor)
            injectVisibilityCheckCall(descriptor)
        }
    }
}
