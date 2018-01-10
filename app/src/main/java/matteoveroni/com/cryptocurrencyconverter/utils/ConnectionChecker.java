package matteoveroni.com.cryptocurrencyconverter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;

/**
 * @author Matteo Veroni
 */

public class ConnectionChecker {

    private final ConnectivityManager connectivityManager;

    private static final long TIMER_INTERVAL_IN_MILLIS = 10000;

    public interface NetworkConnectionObserver {
        void readNetworkResponse(boolean isConnectedToWeb);
    }

    public ConnectionChecker(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void startToCheckConnection(final NetworkConnectionObserver netObserver) {
        checkConnection(netObserver);
    }

    private void checkConnection(final NetworkConnectionObserver netObserver) {
        new CountDownTimer(TIMER_INTERVAL_IN_MILLIS, TIMER_INTERVAL_IN_MILLIS) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                boolean networkResponse = isConnectedToNetwork();
                netObserver.readNetworkResponse(networkResponse);
                checkConnection(netObserver);
            }
        }.start();
    }

    public boolean isConnectedToNetwork() {
        if (connectivityManager == null)
            return false;

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


}
