package com.cityrally.app.game;

import android.content.Intent;
import android.os.Parcelable;
import android.provider.MediaStore;
import com.cityrally.app.manager.Manager;

/**
 * Created by po on 12/17/14.
 */
public class PhotoGame extends Game {
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onStart() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Manager.activity().getPackageManager()) != null) {
            Manager.activity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onResult(boolean success) {
        if (this.gameResultListener != null) {
            this.gameResultListener.onResult(success);
        }
    }
}
