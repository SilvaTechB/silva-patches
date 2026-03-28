/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.patches.youtube.layout.hide.shelves

import app.morphe.patcher.patch.bytecodePatch
import app.silva.patches.youtube.misc.engagement.engagementPanelHookPatch
import app.silva.patches.youtube.misc.extension.sharedExtensionPatch
import app.silva.patches.youtube.misc.litho.filter.addLithoFilter
import app.silva.patches.youtube.misc.litho.filter.lithoFilterPatch
import app.silva.patches.youtube.misc.litho.observer.layoutReloadObserverPatch
import app.silva.patches.youtube.misc.navigation.navigationBarHookPatch
import app.silva.patches.youtube.misc.playertype.playerTypeHookPatch

private const val FILTER_CLASS_DESCRIPTOR =
    "Lapp/silva/extension/youtube/patches/components/HorizontalShelvesFilter;"

internal val hideHorizontalShelvesPatch = bytecodePatch {
    dependsOn(
        sharedExtensionPatch,
        lithoFilterPatch,
        playerTypeHookPatch,
        navigationBarHookPatch,
        engagementPanelHookPatch,
        layoutReloadObserverPatch,
    )

    execute {
        addLithoFilter(FILTER_CLASS_DESCRIPTOR)
    }
}
