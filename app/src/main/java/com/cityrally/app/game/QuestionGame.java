package com.cityrally.app.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.cityrally.app.R;
import com.cityrally.app.manager.Manager;

/**
 * Created by po on 12/25/14.
 */
public class QuestionGame extends Game {
    @Override
    protected void onStart() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Manager.activity());
        builderSingle.setTitle(R.string.c_game_3_question);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Manager.activity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(Manager.activity().getString(R.string.c_game_3_a1));
        arrayAdapter.add(Manager.activity().getString(R.string.c_game_3_a2));
        arrayAdapter.add(Manager.activity().getString(R.string.c_game_3_a3));
        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                if (!strName.equals(Manager.activity().getString(R.string.c_game_3_a1))) {
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(Manager.activity());
                    builderInner.setMessage(R.string.wrong);
                    builderInner.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onStart();
                        }
                    });
                    builderInner.show();
                } else {
                    onResult(true);
                }
            }
        });
        builderSingle.show();
    }
}
