/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.patches.reddit.misc.extension.hooks

import app.silva.patches.shared.misc.extension.activityOnCreateExtensionHook

internal val redditActivityOnCreateHook = activityOnCreateExtensionHook(
    activityClassType = "Lcom/reddit/launch/main/MainActivity;",
    targetBundleMethod = true,
)

internal val redditApplicationOnCreateHook = activityOnCreateExtensionHook(
    activityClassType = "Lcom/reddit/frontpage/FrontpageApplication;",
    targetBundleMethod = false,
)
