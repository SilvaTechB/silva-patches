package app.silva.extension.youtube.patches;

import static app.silva.extension.shared.StringRef.sf;

import app.silva.extension.shared.Logger;
import app.silva.extension.shared.Utils;

@SuppressWarnings("unused")
public class AccountCredentialsInvalidTextPatch {

    /**
     * Injection point.
     */
    public static String getOfflineNetworkErrorString(String original) {
        try {
            if (Utils.isNetworkConnected()) {
                Logger.printDebug(() -> "Network appears to be online, but app is showing offline error");
                return '\n' + sf("microg_offline_account_login_error").toString();
            }

            Logger.printDebug(() -> "Network is offline");
        } catch (Exception ex) {
            Logger.printException(() -> "getOfflineNetworkErrorString failure", ex);
        }

        return original;
    }
}
