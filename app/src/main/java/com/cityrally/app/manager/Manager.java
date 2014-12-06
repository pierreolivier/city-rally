package com.cityrally.app.manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Log;
import com.cityrally.app.MainActivity;
import com.google.android.gms.plus.PlusClient;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by po on 11/25/14.
 */
public class Manager {
    private static MainActivity mMainActivity;
    private static LocationManager mLocationManager;

    public Manager() {
        super();
    }

    public static void onCreate(MainActivity mainActivity) {
        Manager.mMainActivity = mainActivity;
        Manager.mLocationManager = new LocationManager();
    }

    public static void onStart() {
        Manager.mLocationManager.connect();
        Log.e("location", "connect");
    }

    public static void onStop() {
        if(Manager.mLocationManager != null) {
            Manager.mLocationManager.disconnect();
        }
        Log.e("location", "disconnect");
    }

    public static void onDestroy() {
        Manager.mLocationManager.disconnect();

        Manager.mMainActivity = null;
        Manager.mLocationManager = null;
    }

    public static MainActivity activity() {
        return Manager.mMainActivity;
    }

    public static LocationManager location() {
        return Manager.mLocationManager;
    }

    public static String getUsername() {
        AccountManager manager = AccountManager.get(Manager.mMainActivity);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            return possibleEmails.get(0);
        } else
            return null;
    }
}
