package app.silva.patches.youtube.misc.announcements

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/announcements/AnnouncementsPatch;"

val announcementsPatch = bytecodePatch(
    // FIXME: Change this to an "Update is available" patch
    //name = "Announcements",
    description = "Adds an option to show announcements from Morphe on app startup.",
) {
    dependsOn(
        settingsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.MISC.addPreferences(
            SwitchPreference("silva_announcements"),
        )

        YouTubeActivityOnCreateFingerprint.method.addInstruction(
            0,
            "invoke-static/range { p0 .. p0 }, $EXTENSION_CLASS_DESCRIPTOR->showAnnouncement(Landroid/app/Activity;)V",
        )
    }
}
