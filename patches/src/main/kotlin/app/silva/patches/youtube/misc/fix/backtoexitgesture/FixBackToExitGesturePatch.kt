/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.silva.patches.youtube.misc.fix.backtoexitgesture

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.playertype.playerTypeHookPatch
import app.silva.patches.youtube.shared.YouTubeMainActivityOnBackPressedFingerprint
import app.silva.util.addInstructionsAtControlFlowLabel
import app.silva.util.getMutableMethod
import app.silva.util.getReference
import app.silva.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/FixBackToExitGesturePatch;"

internal val fixBackToExitGesturePatch = bytecodePatch(
    description = "Fixes the swipe back to exit gesture."
) {
    dependsOn(
        sharedExtensionPatch,
        playerTypeHookPatch
    )

    execute {
        RecyclerViewTopScrollingFingerprint.let {
            it.method.addInstructionsAtControlFlowLabel(
                it.instructionMatches.last().index + 1,
                "invoke-static { }, $EXTENSION_CLASS_DESCRIPTOR->onTopView()V"
            )
        }

        ScrollPositionFingerprint.let {
            it.instructionMatches[1]
                .getInstruction<ReferenceInstruction>()
                .getReference<MethodReference>()!!
                .getMutableMethod()
                .apply {
                    val index = indexOfFirstInstructionOrThrow {
                        opcode == Opcode.INVOKE_VIRTUAL && getReference<MethodReference>()?.definingClass ==
                                "Landroid/support/v7/widget/RecyclerView;"
                    }

                    addInstruction(
                        index,
                        "invoke-static { }, $EXTENSION_CLASS_DESCRIPTOR->onScrollingViews()V"
                    )
                }
        }

        YouTubeMainActivityOnBackPressedFingerprint.let {
            it.clearMatch()
            it.method.apply {
                val index = it.instructionMatches.first().index + 1

                addInstructionsAtControlFlowLabel(
                    index,
                    "invoke-static { p0 }, $EXTENSION_CLASS_DESCRIPTOR->onBackPressed(Landroid/app/Activity;)V"
                )
            }
        }
    }
}
