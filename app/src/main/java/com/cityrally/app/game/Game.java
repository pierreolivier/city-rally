package com.cityrally.app.game;

/**
 * Created by po on 12/17/14.
 */
public abstract class Game {
    protected GameResultListener gameResultListener;

    protected abstract void onStart();

    public void start(GameResultListener gameResultListener) {
        this.gameResultListener = gameResultListener;

        onStart();
    }

    public void onResult(boolean success) {
        if (this.gameResultListener != null) {
            this.gameResultListener.onResult(success);
        }
    }
}
