/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.patches.reddit.misc.openlink

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.reddit.misc.settings.settingsPatch
import app.silva.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import app.silva.util.getMutableMethod
import app.silva.util.getReference
import app.silva.util.setExtensionIsPatchIncluded
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/reddit/patches/OpenLinksDirectlyPatch;"

@Suppress("unused")
val openLinksDirectlyPatch = bytecodePatch(
    name = "Open links directly",
    description = "Adds an option to skip over redirection URLs in external links."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(settingsPatch)

    execute {
        CustomReportsFingerprint.let {
            it.instructionMatches[3]
                .getInstruction<ReferenceInstruction>()
                .getReference<MethodReference>()!!
                .getMutableMethod()
                .addInstructions(
                    0,
                    """
                        invoke-static { p2 }, $EXTENSION_CLASS_DESCRIPTOR->parseRedirectUri(Landroid/net/Uri;)Landroid/net/Uri;
                        move-result-object p2
                    """
                )
        }

        setExtensionIsPatchIncluded(EXTENSION_CLASS_DESCRIPTOR)
    }
}
