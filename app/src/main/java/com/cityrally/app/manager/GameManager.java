package com.cityrally.app.manager;

import android.widget.Toast;
import com.cityrally.app.R;
import com.cityrally.app.game.CompassGame;
import com.cityrally.app.game.Game;
import com.cityrally.app.game.PhotoGame;
import com.cityrally.app.location.SimpleGeofence;
import com.cityrally.app.view.ChallengesFragment;
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
    private HashMap<String, Challenge> mChallenges;
    private HashMap<String, Game> mGames;

    public GameManager() {
        super();

        mGeofences = new HashMap<String, SimpleGeofence>();
        initGeofences();

        mChallenges = new HashMap<String, Challenge>();
        //loadChallenges();
        if (mChallenges.size() == 0) {
            initChallenges();
        }
        reloadChallengesResources();
        saveChallenges();

        mGames = new HashMap<String, Game>();
        initGames();
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
            mChallenges = (HashMap<String, Challenge>) input.readObject();
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
        mGeofences.put("1", new SimpleGeofence( // eiffel tower
                "1",
                48.858366, 2.294460,
                50,
                GEOFENCE_EXPIRATION_TIME, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT));

        mGeofences.put("2", new SimpleGeofence( // arc de triomphe
                "2",
                48.873789, 2.295030,
                50,
                GEOFENCE_EXPIRATION_TIME, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT));

        mGeofences.put("3", new SimpleGeofence( // notre dame
                "3",
                48.853317, 2.348968,
                100,
                GEOFENCE_EXPIRATION_TIME, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT));
    }

    private void initChallenges() {
        mChallenges.put("1", new Challenge("1", "photo", false, false));
        mChallenges.put("2", new Challenge("2", "compass", false, false));
        mChallenges.put("3", new Challenge("3", "question", false, false));
    }

    private void reloadChallengesResources() {
        mChallenges.get("1").setResources(R.drawable.eiffel_tower, R.string.c_title_1, R.string.c_subtitle_1, R.string.c_text_1);
        mChallenges.get("2").setResources(R.drawable.arc, R.string.c_title_2, R.string.c_subtitle_2, R.string.c_text_2);
        mChallenges.get("3").setResources(R.drawable.dame, R.string.c_title_3, R.string.c_subtitle_3, R.string.c_text_3);
    }

    private void initGames() {
        mGames.put("photo", new PhotoGame());
        mGames.put("compass", new CompassGame());
    }

    public Collection<SimpleGeofence> getGeofences() {
        return mGeofences.values();
    }

    public SimpleGeofence getGeofenceWithId(String id) {
        return mGeofences.get(id);
    }

    public Game getGameWithId(String id) {
        return mGames.get(id);
    }

    public Collection<Challenge> getChallenges() {
        return mChallenges.values();
    }

    public HashMap<String, Challenge> getChallengesMap() {
        return mChallenges;
    }

    public void onUnlock(final Challenge challenge, Geofence geofence) {
        if (!challenge.isUnlocked()) {
            challenge.setUnlocked(true);
            saveChallenges();

            Manager.activity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Manager.activity(), R.string.challenge_unlocked, Toast.LENGTH_LONG).show();

                    if (ChallengesFragment.mAdapter != null) {
                        ChallengesFragment.mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
