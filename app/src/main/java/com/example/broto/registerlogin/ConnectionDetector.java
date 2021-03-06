package com.example.broto.registerlogin;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by broto on 1/21/17.
 */

public class ConnectionDetector {

    Context context;

    public ConnectionDetector(Context context){
        this.context=context;
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager=(ConnectivityManager)
                context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null)
                return true;
            else
                return false;

        }
        else
            return false;
    }
}
