package app.silva.patches.music.misc.gms

import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.fileprovider.fileProviderPatch
import app.silva.patches.music.misc.gms.Constants.SILVA_MUSIC_PACKAGE_NAME
import app.silva.patches.music.misc.gms.Constants.MUSIC_PACKAGE_NAME
import app.silva.patches.music.misc.settings.PreferenceScreen
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.misc.spoof.spoofVideoStreamsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.music.shared.MusicActivityOnCreateFingerprint
import app.silva.patches.shared.CastContextFetchFingerprint
import app.silva.patches.shared.PrimeMethodFingerprint
import app.silva.patches.shared.misc.gms.gmsCoreSupportPatch

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = MUSIC_PACKAGE_NAME,
    toPackageName = SILVA_MUSIC_PACKAGE_NAME,
    primeMethodFingerprint = PrimeMethodFingerprint,
    earlyReturnFingerprints = setOf(
        CastContextFetchFingerprint,
    ),
    mainActivityOnCreateFingerprint = MusicActivityOnCreateFingerprint,
    extensionPatch = sharedExtensionPatch,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    dependsOn(spoofVideoStreamsPatch)

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
}

private fun gmsCoreSupportResourcePatch() =
    app.silva.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
        fromPackageName = MUSIC_PACKAGE_NAME,
        toPackageName = SILVA_MUSIC_PACKAGE_NAME,
        spoofedPackageSignature = "afb0fed5eeaebdd86f56a97742f4b6b33ef59875",
        screen = PreferenceScreen.MISC,
        block = {
            dependsOn(
                settingsPatch,
                fileProviderPatch(
                    MUSIC_PACKAGE_NAME,
                    SILVA_MUSIC_PACKAGE_NAME
                )
            )
        }
    )
