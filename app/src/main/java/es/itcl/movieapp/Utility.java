package es.itcl.movieapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Davit.Sisauri on 05/09/2015.
 */
public class Utility {


    public static boolean checkConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo checkInternet = cm.getActiveNetworkInfo();
        if (checkInternet != null
                && checkInternet.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}


