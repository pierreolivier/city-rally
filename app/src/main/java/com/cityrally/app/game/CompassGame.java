package com.cityrally.app.game;

import android.content.Intent;
import android.provider.MediaStore;
import com.cityrally.app.game.compass.CompassActivity;
import com.cityrally.app.manager.Manager;

/**
 * Created by Pierre-Olivier on 18/12/2014.
 */
public class CompassGame extends Game {
    @Override
    protected void onStart() {
        Intent intent = new Intent(Manager.activity(), CompassActivity.class);
        Manager.activity().startActivity(intent);
    }
}
