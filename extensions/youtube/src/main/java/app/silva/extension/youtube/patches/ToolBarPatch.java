/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.extension.youtube.patches;

import android.view.View;
import android.widget.ImageView;

import app.silva.extension.shared.Logger;

@SuppressWarnings("unused")
public class ToolBarPatch {

    /**
     * Injection point.
     */
    public static void hookToolBar(Enum<?> iconEnum, ImageView imageView) {
        if (iconEnum != null && imageView.getParent() instanceof View parentView) {
            String enumName = iconEnum.name();
            Logger.printDebug(() -> "enum: " + enumName);
            hookToolBar(enumName, parentView, imageView);
        }
    }

    private static void hookToolBar(String enumString, View parentView, ImageView imageView) {
        // Code added during patching.
    }
}