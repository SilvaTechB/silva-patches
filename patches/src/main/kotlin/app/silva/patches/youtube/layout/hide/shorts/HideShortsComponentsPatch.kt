/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to Morphe contributions.
 */

package app.silva.patches.youtube.layout.hide.shorts

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.booleanOption
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.silva.patches.shared.misc.mapping.ResourceType
import app.silva.patches.shared.misc.mapping.getResourceId
import app.silva.patches.shared.misc.mapping.resourceMappingPatch
import app.silva.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.silva.patches.shared.misc.settings.preference.SwitchPreference
import app.silva.patches.youtube.misc.engagement.engagementPanelHookPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.litho.filter.addLithoFilter
import app.silva.patches.youtube.misc.litho.filter.lithoFilterPatch
import app.silva.patches.youtube.misc.litho.observer.layoutReloadObserverPatch
import app.silva.patches.youtube.misc.navigation.addBottomBarContainerHook
import app.silva.patches.youtube.misc.navigation.navigationBarHookPatch
import app.silva.patches.youtube.misc.playservice.is_20_07_or_greater
import app.silva.patches.youtube.misc.playservice.is_21_05_or_greater
import app.silva.patches.youtube.misc.playservice.versionCheckPatch
import app.silva.patches.youtube.misc.settings.PreferenceScreen
import app.silva.patches.youtube.misc.settings.settingsPatch
import app.silva.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.silva.util.findElementByAttributeValueOrThrow
import app.silva.util.forEachLiteralValueInstruction
import app.silva.util.getMutableMethod
import app.silva.util.getReference
import app.silva.util.indexOfFirstInstructionOrThrow
import app.silva.util.removeFromParent
import app.silva.util.returnLate
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val hideShortsAppShortcutOption = booleanOption(
    key = "hideShortsAppShortcut",
    default = false,
    title = "Hide Shorts app shortcut",
    description = "Permanently hides the shortcut to open Shorts when long pressing the app icon in your launcher.",
)

internal val hideShortsWidgetOption = booleanOption(
    key = "hideShortsWidget",
    default = false,
    title = "Hide Shorts widget",
    description = "Permanently hides the launcher widget Shorts button.",
)

private val hideShortsComponentsResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        resourceMappingPatch,
        versionCheckPatch,
    )

    execute {
        val hideShortsAppShortcut by hideShortsAppShortcutOption
        val hideShortsWidget by hideShortsWidgetOption

        PreferenceScreen.SHORTS.addPreferences(
            SwitchPreference("silva_hide_shorts_channel"),
            SwitchPreference("silva_hide_shorts_home"),
            SwitchPreference("silva_hide_shorts_search"),
            SwitchPreference("silva_hide_shorts_subscriptions"),
            SwitchPreference("silva_hide_shorts_video_description"),
            SwitchPreference("silva_hide_shorts_history"),

            PreferenceScreenPreference(
                key = "silva_shorts_player_screen",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    // Shorts player components.
                    // Ideally each group should be ordered similar to how they appear in the UI

                    // Vertical row of buttons on right side of the screen.
                    // Like fountain may no longer be used by YT anymore.
                    //SwitchPreference("silva_hide_shorts_like_fountain"),
                    SwitchPreference("silva_hide_shorts_like_button"),
                    SwitchPreference("silva_hide_shorts_dislike_button"),
                    SwitchPreference("silva_hide_shorts_comments_button"),
                    SwitchPreference("silva_hide_shorts_share_button"),
                    SwitchPreference("silva_hide_shorts_remix_button"),
                    SwitchPreference("silva_hide_shorts_sound_button"),

                    // Upper and middle area of the player.
                    SwitchPreference("silva_hide_shorts_join_button"),
                    SwitchPreference("silva_hide_shorts_subscribe_button"),
                    SwitchPreference("silva_hide_shorts_paused_overlay_buttons"),

                    // Suggested actions.
                    SwitchPreference("silva_hide_shorts_preview_comment"),
                    SwitchPreference("silva_hide_shorts_save_sound_button"),
                    SwitchPreference("silva_hide_shorts_use_sound_button"),
                    SwitchPreference("silva_hide_shorts_use_template_button"),
                    SwitchPreference("silva_hide_shorts_upcoming_button"),
                    SwitchPreference("silva_hide_shorts_effect_button"),
                    SwitchPreference("silva_hide_shorts_green_screen_button"),
                    SwitchPreference("silva_hide_shorts_hashtag_button"),
                    SwitchPreference("silva_hide_shorts_live_preview"),
                    SwitchPreference("silva_hide_shorts_new_posts_button"),
                    SwitchPreference("silva_hide_shorts_shop_button"),
                    SwitchPreference("silva_hide_shorts_tagged_products"),
                    SwitchPreference("silva_hide_shorts_search_suggestions"),
                    SwitchPreference("silva_hide_shorts_super_thanks_button"),
                    SwitchPreference("silva_hide_shorts_stickers"),

                    // Bottom of the screen.
                    SwitchPreference("silva_hide_shorts_ai_button"),
                    SwitchPreference("silva_hide_shorts_auto_dubbed_label"),
                    SwitchPreference("silva_hide_shorts_location_label"),
                    SwitchPreference("silva_hide_shorts_channel_bar"),
                    SwitchPreference("silva_hide_shorts_info_panel"),
                    SwitchPreference("silva_hide_shorts_full_video_link_label"),
                    SwitchPreference("silva_hide_shorts_video_title"),
                    SwitchPreference("silva_hide_shorts_sound_metadata_label"),
                    SwitchPreference("silva_hide_shorts_navigation_bar"),
                ),
            )
        )

        // Verify the file has the expected node, even if the patch option is off.
        document("res/xml/main_shortcuts.xml").use { document ->
            val shortsItem = document.childNodes.findElementByAttributeValueOrThrow(
                "android:shortcutId",
                "shorts-shortcut",
            )

            if (hideShortsAppShortcut == true) {
                shortsItem.removeFromParent()
            }
        }

        document("res/layout/appwidget_two_rows.xml").use { document ->
            val shortsItem = document.childNodes.findElementByAttributeValueOrThrow(
                "android:id",
                "@id/button_shorts_container",
            )

            if (hideShortsWidget == true) {
                shortsItem.removeFromParent()
            }
        }
    }
}

private const val FILTER_CLASS_DESCRIPTOR = "Lapp/morphe/extension/youtube/patches/components/ShortsFilter;"

@Suppress("unused")
val hideShortsComponentsPatch = bytecodePatch(
    name = "Hide Shorts components",
    description = "Adds options to hide components related to Shorts."
) {
    dependsOn(
        engagementPanelHookPatch,
        hideShortsComponentsResourcePatch,
        layoutReloadObserverPatch,
        lithoFilterPatch,
        navigationBarHookPatch,
        resourceMappingPatch,
        sharedExtensionPatch,
        versionCheckPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    hideShortsAppShortcutOption()
    hideShortsWidgetOption()

    execute {
        addLithoFilter(FILTER_CLASS_DESCRIPTOR)

        // region Hide sound button.

        if (!is_21_05_or_greater) {
            forEachLiteralValueInstruction(
                getResourceId(ResourceType.DIMEN, "reel_player_right_pivot_v2_size")
            ) { literalInstructionIndex ->
                val targetIndex = indexOfFirstInstructionOrThrow(literalInstructionIndex) {
                    getReference<MethodReference>()?.name == "getDimensionPixelSize"
                } + 1

                val sizeRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 1,
                    """
                        invoke-static { v$sizeRegister }, $FILTER_CLASS_DESCRIPTOR->getSoundButtonSize(I)I
                        move-result v$sizeRegister
                    """
                )
            }
        }

        // endregion

        // region Hide the navigation bar.

        // Set the bottom bar container view.
        addBottomBarContainerHook(
            descriptor = "$FILTER_CLASS_DESCRIPTOR->setBottomBarContainer(Landroid/view/View;)V",
            highPriority = true
        )

        // Set the pivotBar view.
        SetPivotBarVisibilityFingerprint.let { result ->
            result.method.apply {
                val insertIndex = result.instructionMatches.last().index
                val viewRegister = getInstruction<OneRegisterInstruction>(insertIndex - 1).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static {v$viewRegister}," +
                            "$FILTER_CLASS_DESCRIPTOR->setPivotBar(Lcom/google/android/libraries/youtube/rendering/ui/pivotbar/PivotBar;)V",
                )
            }
        }

        // Hook to hide the pivotBar when the Shorts player is opened.
        ReelWatchFragmentInitPlaybackFingerprint.instructionMatches.last()
            .instruction
            .getReference<MethodReference>()!!
            .getMutableMethod()
            .addInstruction(
                0,
                "invoke-static { p1 }, $FILTER_CLASS_DESCRIPTOR->hidePivotBar(Ljava/lang/String;)V",
            )

        // Hide the bottom bar container of the Shorts player.
        ShortsBottomBarContainerFingerprint.let {
            it.method.apply {
                val targetIndex = it.instructionMatches.last().index
                val heightRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 1,
                    """
                        invoke-static { v$heightRegister }, $FILTER_CLASS_DESCRIPTOR->getNavigationBarHeight(I)I
                        move-result v$heightRegister
                    """
                )
            }
        }

        // endregion


        // region Disable experimental Shorts flags.

        // Flags might be present in earlier targets, but they are not found in 19.47.53.
        // If these flags are forced on, the experimental layout is still not used, and
        // it appears the features requires additional server side data to fully use.
        if (is_20_07_or_greater) {
            // Experimental Shorts player uses Android native buttons and not Litho,
            // and the layout is provided by the server.
            //
            // Since the buttons are native components and not Litho, it should be possible to
            // fix the RYD Shorts loading delay by asynchronously loading RYD and updating
            // the button text after RYD has loaded.
            ShortsExperimentalPlayerFeatureFlagFingerprint.method.returnLate(false)

            // Experimental UI renderer must also be disabled since it requires the
            // experimental Shorts player. If this is enabled but Shorts player
            // is disabled then the app crashes when the Shorts player is opened.
            RenderNextUIFeatureFlagFingerprint.method.returnLate(false)
        }

        // endregion
    }
}
