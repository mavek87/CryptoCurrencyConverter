package matteoveroni.com.cryptocurrencyconverter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Matteo Veroni
 */

public class ConnectionChecker {

    private final ConnectivityManager connectivityManager;

    public ConnectionChecker(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnectedToNetwork() {
        if (connectivityManager == null)
            return false;

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
