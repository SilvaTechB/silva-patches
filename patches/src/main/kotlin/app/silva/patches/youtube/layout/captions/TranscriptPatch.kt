package app.silva.patches.youtube.layout.captions

import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.contexthook.Endpoint
import app.silva.patches.youtube.misc.contexthook.addClientVersionHook
import app.silva.patches.youtube.misc.contexthook.clientContextHookPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playservice.is_20_06_or_greater
import app.silva.patches.youtube.misc.playservice.versionCheckPatch
import app.silva.patches.youtube.misc.settings.settingsPatch

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/youtube/patches/TranscriptPatch;"

internal val transcriptPatch = bytecodePatch(
    description = "Add an option to fix an issue where transcript is unavailable due to a precondition check failure.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        versionCheckPatch,
        clientContextHookPatch,
    )

    execute {
        if (!is_20_06_or_greater) {
            return@execute
        }

        settingsMenuCaptionGroup.add(
            SwitchPreference("silva_fix_transcript")
        )

        addClientVersionHook(
            Endpoint.TRANSCRIPT,
            "$EXTENSION_CLASS_DESCRIPTOR->getTranscriptAppVersionOverride(Ljava/lang/String;)Ljava/lang/String;",
        )
    }
}
