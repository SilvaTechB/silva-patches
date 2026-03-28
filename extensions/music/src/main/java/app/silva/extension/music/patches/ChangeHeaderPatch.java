/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.silva.extension.music.patches;

import java.util.Objects;

import app.silva.extension.shared.Logger;
import app.silva.extension.shared.ResourceType;
import app.silva.extension.shared.ResourceUtils;
import app.silva.extension.music.settings.Settings;

@SuppressWarnings("unused")
public class ChangeHeaderPatch {

    public enum HeaderLogo {
        DEFAULT(null),
        MORPHE("silva_header_dark"),
        CUSTOM("silva_header_custom_dark");

        private final String drawableName;

        HeaderLogo(String drawableName) {
            this.drawableName = drawableName;
        }

        private Integer getDrawableId() {
            if (drawableName == null) {
                return null;
            }

            int id = ResourceUtils.getIdentifier(ResourceType.DRAWABLE, drawableName);
            if (id == 0) {
                Logger.printException(() ->
                        "Header drawable not found: " + drawableName
                );
                Settings.HEADER_LOGO.resetToDefault();
                return null;
            }

            return id;
        }
    }

    /**
     * Injection point.
     */
    public static int getHeaderDrawableId(int original) {
        return Objects.requireNonNullElse(
                Settings.HEADER_LOGO.get().getDrawableId(),
                original
        );
    }
}
