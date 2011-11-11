package fr.pronoschallenge.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 02/08/11
 * Time: 22:04
 */
public class NetworkUtil {
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            NetworkInfo.State networkState = networkInfo.getState();
            if (networkState.compareTo(NetworkInfo.State.CONNECTED) == 0) {
                return true;
            }
        }
        return false;
    }

}
