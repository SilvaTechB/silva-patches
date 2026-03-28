package app.silva.patches.youtube.interaction.loop

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playercontrols.addBottomControl
import app.silva.patches.youtube.misc.playercontrols.initializeBottomControl
import app.silva.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.silva.patches.youtube.misc.playercontrols.playerControlsPatch
import app.silva.patches.youtube.misc.playercontrols.playerControlsResourcePatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.util.ResourceGroup
import app.silva.util.copyResources

private val loopVideoButtonResourcePatch = resourcePatch {
    dependsOn(playerControlsResourcePatch)

    execute {
        copyResources(
            "loopvideobutton",
            ResourceGroup(
                "drawable",
                "silva_loop_video_button_on.xml",
                "silva_loop_video_button_off.xml"
            )
        )

        addBottomControl("loopvideobutton")
    }
}

private const val LOOP_VIDEO_BUTTON_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/videoplayer/LoopVideoButton;"

internal val loopVideoButtonPatch = bytecodePatch(
    description = "Adds the option to display loop video button in the video player.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        loopVideoButtonResourcePatch,
        playerControlsPatch,
    )

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            SwitchPreference("silva_loop_video_button"),
        )

        // Initialize the button using standard approach.
        initializeBottomControl(LOOP_VIDEO_BUTTON_CLASS_DESCRIPTOR)
        injectVisibilityCheckCall(LOOP_VIDEO_BUTTON_CLASS_DESCRIPTOR)
    }
}
