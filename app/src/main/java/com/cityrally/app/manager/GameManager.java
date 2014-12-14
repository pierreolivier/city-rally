package com.cityrally.app.manager;

import com.cityrally.app.R;
import com.cityrally.app.location.SimpleGeofence;
import com.google.android.gms.location.Geofence;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Pierre-Olivier on 06/12/2014.
 */
public class GameManager {
    private final String CHALLENGES_FILENAME = "save.dat";
    private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_TIME = GEOFENCE_EXPIRATION_IN_HOURS * SECONDS_PER_HOUR * MILLISECONDS_PER_SECOND;

    private HashMap<String, SimpleGeofence> mGeofences;
    private ArrayList<Challenge> mChallenges;

    public GameManager() {
        super();

        mGeofences = new HashMap<String, SimpleGeofence>();
        initGeofences();

        mChallenges = new ArrayList<Challenge>();
        /*loadChallenges();
        if (mChallenges.size() == 0) {
            initChallenges();
        }*/
        initChallenges();
    }

    public void saveChallenges() {
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(Manager.activity().getFilesDir(),"") + File.separator + CHALLENGES_FILENAME));
            out.writeObject(mChallenges);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadChallenges() {
        ObjectInputStream input;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(new File(Manager.activity().getFilesDir(),"") + File.separator + CHALLENGES_FILENAME)));
            mChallenges = (ArrayList<Challenge>) input.readObject();
            input.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initGeofences() {
        mGeofences.clear();
        mGeofences.put("1", new SimpleGeofence(
                "1 test",
                49.497018,
                5.980233,
                100,
                GEOFENCE_EXPIRATION_TIME,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT));
    }

    private void initChallenges() {
        mChallenges.add(new Challenge(R.drawable.eiffel_tower, R.string.c_title_1, R.string.c_subtitle_1, R.string.c_text_1, "1", "photo", true, true));
    }

    public Collection<SimpleGeofence> getGeofences() {
        return mGeofences.values();
    }

    public SimpleGeofence getGeofenceWithId(String id) {
        return mGeofences.get(id);
    }

    public ArrayList<Challenge> getChallenges() {
        return mChallenges;
    }
}
