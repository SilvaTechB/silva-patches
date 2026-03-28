/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.patches.youtube.layout.hide.updatescreen

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string

internal object AppBlockingCheckResultToStringFingerprint : Fingerprint(
    name = "toString",
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("AppBlockingCheckResult{intent=")
    )
)
