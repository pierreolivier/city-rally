package com.cityrally.app.manager;

import com.cityrally.app.location.SimpleGeofence;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Pierre-Olivier on 06/12/2014.
 */
public class GameManager {
    private final String CHALLENGES_FILENAME = "save.dat";

    private HashMap<String, SimpleGeofence> mGeofences;
    private ArrayList<Challenge> mChallenges;

    public GameManager() {
        super();

        mGeofences = new HashMap<String, SimpleGeofence>();
        initGeofences();

        mChallenges = new ArrayList<Challenge>();
        loadChallenges();
        if (mChallenges.size() == 0) {
            initChallenges();
        }
    }

    private void initGeofences() {

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
    private void initChallenges() {

    }

    public Collection<SimpleGeofence> getGeofences() {
        return mGeofences.values();
    }

    public SimpleGeofence getGeofenceWithId(String id) {
        return mGeofences.get(id);
    }
}
