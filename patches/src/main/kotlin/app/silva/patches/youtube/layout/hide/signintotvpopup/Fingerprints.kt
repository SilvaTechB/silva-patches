package app.silva.patches.youtube.layout.hide.signintotvpopup

import app.morphe.patcher.Fingerprint
import app.silva.patches.shared.misc.mapping.ResourceType
import app.silva.patches.shared.misc.mapping.resourceLiteral

internal object SignInToTVPopupFingerprint : Fingerprint(
    returnType = "Z",
    parameters = listOf("Ljava/lang/String;", "Z", "L"),
    filters = listOf(
        resourceLiteral(
            ResourceType.STRING,
            "mdx_seamless_tv_sign_in_drawer_fragment_title"
        )
    )
)