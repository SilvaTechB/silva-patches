/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.patches.reddit.layout.modern

import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.reddit.misc.settings.settingsPatch
import app.silva.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import app.silva.util.addInstructionsAtControlFlowLabel
import app.silva.util.findInstructionIndicesReversedOrThrow
import app.silva.util.setExtensionIsPatchIncluded
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/morphe/extension/reddit/patches/DisableModernHomePatch;"

@Suppress("unused")
val disableModernHomePatch = bytecodePatch(
    name = "Disable modern home",
    description = "Adds an option to disable the modern home UI."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(settingsPatch)

    execute {

        HomeRevampVariantFingerprint.method.apply {
            findInstructionIndicesReversedOrThrow(Opcode.RETURN).forEach { index ->
                val register = getInstruction<OneRegisterInstruction>(index).registerA

                addInstructionsAtControlFlowLabel(
                    index,
                    """
                        invoke-static { v$register }, $EXTENSION_CLASS_DESCRIPTOR->disableModernHome(Z)Z
                        move-result v$register 
                    """
                )
            }
        }

        setExtensionIsPatchIncluded(EXTENSION_CLASS_DESCRIPTOR)
    }
}
