package com.cityrally.app.manager;

import com.cityrally.app.location.SimpleGeofence;
import com.google.android.gms.location.Geofence;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Pierre-Olivier on 06/12/2014.
 */
public class Challenge implements Serializable {
    private static final long serialVersionUID = -29238982928391L;

    private int image;
    private int title;
    private int subtitle;
    private int text;
    private String geofenceId;

    private boolean unlocked;
    private boolean solved;

    public Challenge(int image, int title, int subtitle, int text, String geofenceId, boolean unlocked, boolean solved) {
        this.image = image;
        this.title = title;
        this.subtitle = subtitle;
        this.text = text;
        this.geofenceId = geofenceId;
        this.unlocked = unlocked;
        this.solved = solved;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(int subtitle) {
        this.subtitle = subtitle;
    }

    public int getText() {
        return text;
    }

    public void setText(int text) {
        this.text = text;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public SimpleGeofence getGeofence() {
        return Manager.game().getGeofenceWithId(this.geofenceId);
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "image=" + image +
                ", title=" + title +
                ", subtitle=" + subtitle +
                ", text=" + text +
                ", geofenceId='" + geofenceId + '\'' +
                ", unlocked=" + unlocked +
                ", solved=" + solved +
                '}';
    }
}
