/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.silva.patches.youtube.interaction.reload

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.playercontrols.addTopControl
import app.silva.patches.youtube.misc.playercontrols.initializeTopControl
import app.silva.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.silva.patches.youtube.misc.playercontrols.playerControlsPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.patches.youtube.shared.YouTubeActivityOnCreateFingerprint
import app.silva.patches.youtube.video.information.videoInformationPatch
import app.silva.util.ResourceGroup
import app.silva.util.copyResources
import app.silva.util.getReference
import app.silva.util.indexOfFirstInstructionReversedOrThrow
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.util.MethodUtil

private val reloadVideoResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        playerControlsPatch,
    )

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            SwitchPreference("silva_reload_video"),
        )

        copyResources(
            "reloadbutton",
            ResourceGroup(
                resourceDirectoryName = "drawable",
                "silva_reload_video.xml",
            ),
        )
    }
}

private const val EXTENSION_BUTTON_DESCRIPTOR =
    "Lapp/silva/extension/youtube/videoplayer/ReloadVideoButton;"

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/ReloadVideoPatch;"

private const val EXTENSION_PLAYER_INTERFACE =
    "Lapp/silva/extension/youtube/patches/ReloadVideoPatch\$PlayerInterface;"

@Suppress("unused")
val reloadVideoPatch = bytecodePatch(
    name = "Reload video",
    description = "Adds options to display buttons in the video player to reload video.",
) {
    dependsOn(
        reloadVideoResourcePatch,
        playerControlsPatch,
        videoInformationPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        initializeTopControl(EXTENSION_BUTTON_DESCRIPTOR)
        injectVisibilityCheckCall(EXTENSION_BUTTON_DESCRIPTOR)

        // Main activity is used to launch downloader intent.
        YouTubeActivityOnCreateFingerprint.method.addInstruction(
            0,
            "invoke-static/range { p0 .. p0 }, $EXTENSION_CLASS_DESCRIPTOR->setMainActivity(Landroid/app/Activity;)V"
        )

        val dismissPlayerInnerMethod = MiniAppOpenYtContentCommandEndpointFingerprint
            .instructionMatches[2]
            .getInstruction<ReferenceInstruction>()
            .getReference<MethodReference>()!!

        mutableClassDefBy(dismissPlayerInnerMethod.definingClass).apply {
            // Add interface and helper methods to allow extension code to call obfuscated methods.
            interfaces.add(EXTENSION_PLAYER_INTERFACE)
            // Add methods to access obfuscated player methods.
            methods.add(
                ImmutableMethod(
                    type,
                    "patch_dismissPlayer",
                    listOf(),
                    "V",
                    AccessFlags.PUBLIC.value or AccessFlags.FINAL.value,
                    null,
                    null,
                    MutableMethodImplementation(2),
                ).toMutable().apply {
                    addInstructions(
                        0,
                        """
                            invoke-virtual { p0 }, $dismissPlayerInnerMethod
                            return-void
                        """
                    )
                }
            )

            methods.single { method ->
                MethodUtil.isConstructor(method)
            }.apply {
                val index = indexOfFirstInstructionReversedOrThrow(Opcode.RETURN_VOID)

                addInstruction(
                    index,
                    "invoke-static/range { p0 .. p0 }, $EXTENSION_CLASS_DESCRIPTOR->initialize($EXTENSION_PLAYER_INTERFACE)V"
                )
            }
        }
    }

    finalize {
        addTopControl(
            "reloadbutton",
            "@+id/silva_reload_video_button",
            "@+id/silva_reload_video_button"
        )
    }
}
