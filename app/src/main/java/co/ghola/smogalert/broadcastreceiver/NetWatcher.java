package co.ghola.smogalert.broadcastreceiver;

/**
 * Created by gholadr on 8/16/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import org.greenrobot.eventbus.EventBus;

import co.ghola.smogalert.MainActivity;
import co.ghola.smogalert.utils.Constants;

/**
 * Created by gholadr on 3/1/16. restarts service in case of connectivity loss due to network disconnect.
 */

public class NetWatcher extends BroadcastReceiver {
    private final String TAG = NetWatcher.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        //here, check that the network connection is available. If yes, start your service. If not, stop your service.

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            EventBus.getDefault().post(Constants.ONLINE);
        }
    }
}
