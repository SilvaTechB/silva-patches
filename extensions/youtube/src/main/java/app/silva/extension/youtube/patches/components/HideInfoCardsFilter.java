package app.silva.extension.youtube.patches.components;

import app.silva.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class HideInfoCardsFilter extends Filter {

    public HideInfoCardsFilter() {
        addIdentifierCallbacks(
                new StringFilterGroup(
                        Settings.HIDE_INFO_CARDS,
                        "info_card_teaser_overlay.e"
                )
        );
    }
}
