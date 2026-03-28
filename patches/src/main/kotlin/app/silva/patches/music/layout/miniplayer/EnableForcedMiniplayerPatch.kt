/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.patches.music.layout.miniplayer

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.misc.settings.PreferenceScreen
import app.silva.patches.music.misc.settings.settingsPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.util.getReference
import app.silva.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/morphe/extension/music/patches/EnableForcedMiniplayerPatch;"

@Suppress("unused")
val enableForcedMiniplayerPatch = bytecodePatch(
    name = "Enable forced miniplayer",
    description = "Adds an option to enable forced miniplayer when switching between music videos, podcasts, or songs."
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            SwitchPreference("silva_music_enable_forced_miniplayer")
        )

        MinimizedPlayerFingerprint.let {
            val method = it.method
            val invokeIndex = method.indexOfFirstInstructionOrThrow {
                opcode == Opcode.INVOKE_VIRTUAL &&
                        getReference<MethodReference>()?.name == "booleanValue"
            }

            val moveResultIndex = invokeIndex + 1
            val moveResultInstr = method.getInstruction<OneRegisterInstruction>(moveResultIndex)
            val targetRegister = moveResultInstr.registerA

            method.addInstructions(
                moveResultIndex + 1,
                """
                    invoke-static {v$targetRegister}, $EXTENSION_CLASS_DESCRIPTOR->enableForcedMiniplayerPatch(Z)Z
                    move-result v$targetRegister
                """
            )
        }
    }
}