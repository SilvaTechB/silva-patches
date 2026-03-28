package app.silva.patches.shared.misc.checks

import app.morphe.patcher.Fingerprint

internal object PatchInfoFingerprint : Fingerprint(
    definingClass = "Lapp/silva/extension/shared/checks/PatchInfo;"
)

internal object PatchInfoBuildFingerprint : Fingerprint(
    definingClass = "Lapp/silva/extension/shared/checks/PatchInfo\$Build;"
)
