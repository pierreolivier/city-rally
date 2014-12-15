package com.cityrally.app.location;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import com.cityrally.app.R;
import com.cityrally.app.manager.Manager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Pierre-Olivier on 04/12/2014.
 */
public class ReceiveTransitionsIntentService extends IntentService {
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationClient.hasError(intent)) {
            int errorCode = LocationClient.getErrorCode(intent);

            Log.e("ReceiveTransitionsIntentService", "Location Services error: " + Integer.toString(errorCode));

            checkLocation();
        } else {
            int transitionType = LocationClient.getGeofenceTransition(intent);

            Log.e("ReceiveTransitionsIntentService", "onHandleIntent");

            // Test that a valid transition was reported
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER || transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);

                /*String[] triggerIds = new String[triggerList.size()];

                for (int i = 0; i < triggerIds.length; i++) {
                    triggerIds[i] = triggerList.get(i).getRequestId();
                }
                Log.e("geofences", "ok " + Arrays.toString(triggerIds));*/

                for (Geofence geofence : triggerList) {
                    if (Manager.location() != null) {
                        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                            Manager.location().onGeofenceEnter(geofence);
                        } else {
                            Manager.location().onGeofenceExit(geofence);
                        }
                    }
                }
            } else {
                Log.e("ReceiveTransitionsIntentService", "Geofence transition error: ");
            }
        }
    }

    private void checkLocation() {
        LocationManager locationManager = null;
        boolean gps = false;
        boolean network = false;

        if(locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {

        }

        Log.e("location", "enable : " + gps);

        try {
            network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {

        }

        if(!gps && !network && Manager.activity() != null) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(Manager.activity());
            dialog.setCancelable(false);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    final Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Manager.activity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(myIntent);
                        }
                    });

                    checkLocation();
                }
            });
            dialog.setNegativeButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    checkLocation();
                }
            });
            Manager.activity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        } else {
            Manager.location().connect();
        }
    }
}
