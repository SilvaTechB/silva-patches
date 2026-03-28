/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaTechB/silva-patches
 *
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */
package app.silva.extension.reddit.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import app.silva.extension.reddit.settings.preference.RedditPreferenceFragment;
import app.silva.extension.reddit.ui.SilvaSettingsIconVectorDrawable;

@SuppressWarnings({"deprecation", "unused"})
public class RedditActivityHook {
    private static final Drawable SILVA_ICON = SilvaSettingsIconVectorDrawable.getIcon();
    private static final String SILVA_LABEL = "Silva";

    /**
     * Injection point.
     */
    public static Drawable getSettingIcon() {
        return SILVA_ICON;
    }

    /**
     * Injection point.
     */
    public static String getSettingLabel() {
        return SILVA_LABEL;
    }

    /**
     * Injection point.
     */
    public static boolean hook(Activity activity) {
        Intent intent = activity.getIntent();
        if (SILVA_LABEL.equals(intent.getStringExtra("com.reddit.extra.initial_url"))) {
            initialize(activity);
            return true;
        }
        return false;
    }

    /**
     * Injection point.
     */
    public static void initialize(Activity activity) {
        int fragmentId = View.generateViewId();
        FrameLayout fragment = new FrameLayout(activity);
        fragment.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        fragment.setId(fragmentId);

        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setFitsSystemWindows(true);
        linearLayout.setTransitionGroup(true);
        linearLayout.addView(fragment);
        linearLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        activity.setContentView(linearLayout);

        activity.getFragmentManager()
                .beginTransaction()
                .replace(fragmentId, new RedditPreferenceFragment())
                .commit();
    }

    /**
     * Injection point.
     */
    public static boolean isAcknowledgment(Enum<?> e) {
        return e != null && "ACKNOWLEDGMENTS".equals(e.name());
    }

    /**
     * Injection point.
     */
    public static Intent initializeByIntent(Context context) {
        Intent intent = new Intent();
        intent.setClassName(context, "com.reddit.webembed.browser.WebBrowserActivity");
        intent.putExtra("com.reddit.extra.initial_url", SILVA_LABEL);
        return intent;
    }
}
