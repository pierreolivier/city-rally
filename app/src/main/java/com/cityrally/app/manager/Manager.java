package com.cityrally.app.manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import com.cityrally.app.MainActivity;
import com.google.android.gms.plus.PlusClient;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by po on 11/25/14.
 */
public class Manager {
    private static MainActivity mMainActivity;

    public Manager() {
        super();
    }

    public static void onCreate(MainActivity mainActivity) {
        Manager.mMainActivity = mainActivity;
    }

    public static void onStart() {

    }

    public static void onStop() {

    }

    public static void onDestroy() {
        Manager.mMainActivity = null;
    }

    public static String getUsername() {
        AccountManager manager = AccountManager.get(Manager.mMainActivity);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type
            // values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            /*String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return null;*/
            return email;
        } else
            return null;
    }
}
