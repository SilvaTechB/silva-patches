/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.patches.reddit.misc.extension

import app.silva.patches.reddit.misc.extension.hooks.redditActivityOnCreateHook
import app.silva.patches.reddit.misc.extension.hooks.redditApplicationOnCreateHook
import app.silva.patches.shared.misc.extension.sharedExtensionPatch

val sharedExtensionPatch = sharedExtensionPatch(
    "reddit",
    false,
    redditActivityOnCreateHook,
    redditApplicationOnCreateHook
)
