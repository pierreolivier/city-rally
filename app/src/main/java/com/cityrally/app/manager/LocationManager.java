package com.cityrally.app.manager;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by po on 10/14/14.
 */
public class LocationManager implements GooglePlayServicesClient.ConnectionCallbacks,  GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 3 * 1000;

    private LocationClient mLocationClient;
    private final LocationRequest mLocationRequest;
    private Location mLastLocation;
    private String mLastLocationAddress;

    private GoogleMap mMap;
    private boolean mCenterOnLocation;

    public LocationManager() {
        super();

        mLocationClient = new LocationClient(Manager.activity(), this, this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLastLocation = null;
        mCenterOnLocation = false;
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
    }

    @Override
    public void onDisconnected() {
        Log.e("location m", "disconnected");
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
}
