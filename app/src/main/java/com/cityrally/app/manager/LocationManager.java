package com.cityrally.app.manager;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.cityrally.app.location.ReceiveTransitionsIntentService;
import com.cityrally.app.location.SimpleGeofence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by po on 10/14/14.
 */
public class LocationManager implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, LocationClient.OnAddGeofencesResultListener, LocationClient.OnRemoveGeofencesResultListener {

    private static final long UPDATE_INTERVAL = 3 * 1000;
    private static final long FASTEST_INTERVAL = 2 * 1000;

    private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_TIME = GEOFENCE_EXPIRATION_IN_HOURS * SECONDS_PER_HOUR * MILLISECONDS_PER_SECOND;

    private LocationClient mLocationClient;
    private final LocationRequest mLocationRequest;
    private Location mLastLocation;
    private String mLastLocationAddress;

    private GoogleMap mMap;
    private boolean mCenterOnLocation;

    private boolean mMoveCamera;
    private double mMoveLatitude;
    private double mMoveLongitude;

    private HashMap<String, Marker> mMarkers;
    private Runnable mAddMarkerAction;

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
        mMoveCamera = false;
        mMoveLatitude = 0.0d;
        mMoveLongitude = 0.0d;

        mMarkers = new HashMap<String, Marker>();
        mAddMarkerAction = null;

        mTransitionPendingIntent = getTransitionPendingIntent();
    }

    public void onCreate() {

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

        // mLocationClient.setMockMode(true);

        startLocationUpdate();

        startGeofencesUpdate();
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

        /*if (mCenterOnLocation) {
            mCenterOnLocation = false;

            moveCamera(location);
        }*/
        if (mMoveCamera && mMap != null) {
            mMoveCamera = false;
            mCenterOnLocation = false;
            moveCamera(mMoveLatitude, mMoveLongitude);
        } else if (mCenterOnLocation && mMap != null) {
            mCenterOnLocation = false;
            moveCamera(location);
        }

        if(mAddMarkerAction != null) {
            mAddMarkerAction.run();
            mAddMarkerAction = null;
        }
    }

    public void centerOnLocation() {
        /*if (mLastLocation != null) {
            moveCamera(mLastLocation);
        } else {
            mCenterOnLocation = true;
        }*/
        if (mLastLocation != null) {
            moveCamera(mLastLocation);
        } else {
            mCenterOnLocation = true;
        }
    }

    public void moveCamera(double latitude, double longitude) {
        moveCamera(latitude, longitude, false);
    }

    public void moveCamera(Location location) {
        moveCamera(location.getLatitude(), location.getLongitude(), false);
    }

    public void moveCamera(double latitude, double longitude, boolean set) {
        if (mMap != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            final CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
            mMap.stopAnimation();
            if (set) {
                mMap.moveCamera(center);
                mMap.moveCamera(zoom);
            } else {
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
        } else {
            mMoveCamera = true;
            mMoveLatitude = latitude;
            mMoveLongitude = longitude;
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

    private void startGeofencesUpdate() {
        List<Geofence> geofences = new ArrayList<Geofence>();
        for (SimpleGeofence geofence : Manager.game().getGeofences()) {
            geofences.add(geofence.toGeofence());
        }
        mLocationClient.addGeofences(geofences, mTransitionPendingIntent, this);
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
        Intent intent = new Intent(Manager.activity(), ReceiveTransitionsIntentService.class);

        return PendingIntent.getService(Manager.activity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int i, String[] strings) {

    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int i, PendingIntent pendingIntent) {

    }

    public void onGeofenceEnter(Geofence geofence) {
        Log.e("enter", geofence.getRequestId());

        Challenge challenge = Manager.game().getChallengesMap().get(geofence.getRequestId());

        if (challenge != null) {
            Manager.game().onUnlock(challenge, geofence);
        }
    }

    public void onGeofenceExit(Geofence geofence) {
        Log.e("exit", geofence.getRequestId());
    }

    public void setDefaultLocation() {
        moveCamera(48.865569d, 2.321180d);
    }

    public void addMarker(final String id, final String title, final double latitude, final double longitude) {
        if (mMap != null) {
            mMarkers.put(id, mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title)));
        } else {
            mAddMarkerAction = new Runnable() {
                @Override
                public void run() {
                    mMarkers.put(id, mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title)));
                }
            };
        }
    }

    public void removeMarker(String id) {
        Marker marker = mMarkers.get(id);
        if (marker != null) {
            marker.remove();
            mMarkers.remove(id);
        }
    }

    public Location createLocation(double lat, double lng, float accuracy) {
        Location newLocation = new Location("network");
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);
        newLocation.setTime(new Date().getTime());
        newLocation.setAccuracy(3.0f);
        newLocation.setElapsedRealtimeNanos(System.nanoTime());
        return newLocation;
    }

    public void setMockLocation(double latitude, double longitude) {
        Location location = createLocation(latitude, longitude, 3);
        //mLocationClient.setMockLocation(location);
        //onLocationChanged(location);
        //Log.e("mock", "new location");
    }

    public void startScenario1() {
        new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    setMockLocation(48.862662, 2.311180);
                    sleep(2000);
                    setMockLocation(48.862407, 2.301825);
                    sleep(10000);
                    setMockLocation(48.858596, 2.293757);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
