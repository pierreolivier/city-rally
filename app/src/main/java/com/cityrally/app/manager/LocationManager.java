package com.cityrally.app.manager;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.cityrally.app.location.ReceiveTransitionsIntentService;
import com.cityrally.app.location.SimpleGeofence;
import com.cityrally.app.location.SimpleGeofenceStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by po on 10/14/14.
 */
public class LocationManager implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, LocationClient.OnAddGeofencesResultListener, LocationClient.OnRemoveGeofencesResultListener {

    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 3 * 1000;

    private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_TIME =
            GEOFENCE_EXPIRATION_IN_HOURS *
                    SECONDS_PER_HOUR *
                    MILLISECONDS_PER_SECOND;

    private LocationClient mLocationClient;
    private final LocationRequest mLocationRequest;
    private Location mLastLocation;
    private String mLastLocationAddress;

    private GoogleMap mMap;
    private boolean mCenterOnLocation;

    private List<Geofence> mGeofenceList;
    private SimpleGeofenceStore mGeofenceStorage;
    private PendingIntent mTransitionPendingIntent;

    public LocationManager() {
        super();

        mLocationClient = new LocationClient(Manager.activity(), this, this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLastLocation = null;
        mCenterOnLocation = false;

        mGeofenceStorage = new SimpleGeofenceStore(Manager.activity());
        mGeofenceList = new ArrayList<Geofence>();
        createGeofences();
    }

    public void connect() {
        mLocationClient.connect();
    }

    public void disconnect() {
        stopLocationUpdate();

        mLocationClient.disconnect();
    }

    public void startLocationUpdate() {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    public void stopLocationUpdate() {
        if (mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("location m", "connected");

        startLocationUpdate();

        mTransitionPendingIntent = getTransitionPendingIntent();
        mLocationClient.addGeofences(mGeofenceList, mTransitionPendingIntent, this);
    }

    @Override
    public void onDisconnected() {
        Log.e("location m", "disconnected");

        mLocationClient.removeGeofences(mTransitionPendingIntent, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(Manager.activity(), 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.v("location", "error: " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());

        mLastLocation = location;

        // new GetAddressTask().execute(mLastLocation);

        Log.v("location test", msg);

        if (mCenterOnLocation) {
            mCenterOnLocation = false;

            moveCamera(location);
        }
    }

    public void centerOnLocation() {
        if (mLastLocation != null) {
            moveCamera(mLastLocation);
        } else {
            mCenterOnLocation = true;
        }
    }

    public void moveCamera(Location location) {
        if (mMap != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            final CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
            mMap.animateCamera(center, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    if (mMap != null) {
                        mMap.animateCamera(zoom);
                    }
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public String getLastLocationAddress() {
        return mLastLocationAddress;
    }

    public void setLastLocationAddress(String mLastLocationAddress) {
        this.mLastLocationAddress = mLastLocationAddress;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void setMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void createGeofences() {
        SimpleGeofence mUIGeofence1  = new SimpleGeofence(
                "1",
                49.497018,
                5.980233,
                100,
                GEOFENCE_EXPIRATION_TIME,
                // This geofence records only entry transitions
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        mGeofenceStorage.setGeofence("1 home", mUIGeofence1);
        mGeofenceList.add(mUIGeofence1.toGeofence());
    }

    @Override
    public void onAddGeofencesResult(int statusCode, String[] strings) {
        if (LocationStatusCodes.SUCCESS == statusCode) {
            Log.e("geofences", "success");
        } else {
            Log.e("geofences", "error " + statusCode);
        }
    }

    private PendingIntent getTransitionPendingIntent() {
        // Create an explicit Intent
        Intent intent = new Intent(Manager.activity(), ReceiveTransitionsIntentService.class);
        /*
         * Return the PendingIntent
         */
        return PendingIntent.getService(Manager.activity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int i, String[] strings) {

    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int i, PendingIntent pendingIntent) {

    }
}
