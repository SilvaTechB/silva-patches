package app.silva.patches.youtube.misc.gms

import app.silva.patches.shared.CastContextFetchFingerprint
import app.silva.patches.shared.PrimeMethodFingerprint
import app.silva.patches.shared.misc.gms.gmsCoreSupportPatch
import app.silva.patches.youtube.layout.buttons.overlay.hidePlayerOverlayButtonsPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.gms.Constants.SILVA_YOUTUBE_PACKAGE_NAME
import app.silva.patches.youtube.misc.gms.Constants.YOUTUBE_PACKAGE_NAME
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.misc.spoof.spoofVideoStreamsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = YOUTUBE_PACKAGE_NAME,
    toPackageName = SILVA_YOUTUBE_PACKAGE_NAME,
    primeMethodFingerprint = PrimeMethodFingerprint,
    earlyReturnFingerprints = setOf(
        CastContextFetchFingerprint,
    ),
    mainActivityOnCreateFingerprint = YouTubeActivityOnCreateFingerprint,
    extensionPatch = sharedExtensionPatch,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    dependsOn(
        hidePlayerOverlayButtonsPatch, // Hide non-functional cast button.
        spoofVideoStreamsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)
}

private fun gmsCoreSupportResourcePatch() =
    app.silva.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
        fromPackageName = YOUTUBE_PACKAGE_NAME,
        toPackageName = SILVA_YOUTUBE_PACKAGE_NAME,
        spoofedPackageSignature = "24bb24c05e47e0aefa68a58a766179d9b613a600",
        screen = PreferenceScreen.MISC,
        block = {
            dependsOn(
                settingsPatch,
                accountCredentialsInvalidTextPatch
            )
        }
    )
