package app.silva.patches.music.layout.theme

import app.silva.patches.music.misc.extension.sharedExtensionPatch
import app.silva.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.silva.patches.shared.layout.theme.THEME_DEFAULT_DARK_COLOR_NAMES
import app.silva.patches.shared.layout.theme.baseThemePatch
import app.silva.patches.shared.layout.theme.baseThemeResourcePatch
import app.silva.patches.shared.layout.theme.darkThemeBackgroundColorOption
import app.silva.patches.shared.misc.settings.overrideThemeColors

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/silva/extension/music/patches/theme/ThemePatch;"

@Suppress("unused")
val themePatch = baseThemePatch(
    extensionClassDescriptor = EXTENSION_CLASS_DESCRIPTOR,

    block = {
        dependsOn(
            sharedExtensionPatch,
            baseThemeResourcePatch(
                darkColorNames = {
                    THEME_DEFAULT_DARK_COLOR_NAMES + setOf(
                        "yt_black_pure",
                        "yt_black_pure_opacity80",
                        "ytm_color_grey_12",
                        "material_grey_800"
                    )
                }
            )
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },

    executeBlock = {
        overrideThemeColors(
            null,
            darkThemeBackgroundColorOption.value!!
        )
    }
)
