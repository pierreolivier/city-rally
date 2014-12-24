package com.cityrally.app.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.cityrally.app.MainActivity;
import com.cityrally.app.R;
import com.cityrally.app.manager.Challenge;
import com.cityrally.app.manager.Manager;

/**
 * Created by Pierre-Olivier on 26/11/2014.
 */
public class MisteryFragment extends Fragment {
    public MisteryFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mistery, container, false);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView hint1 = (TextView) getView().findViewById(R.id.hint1_textview);
        TextView hint2 = (TextView) getView().findViewById(R.id.hint2_textview);
        TextView hint3 = (TextView) getView().findViewById(R.id.hint3_textview);
        Button solveButton = (Button) getView().findViewById(R.id.solveButton);

        Challenge challenge1 = Manager.game().getChallengesMap().get("1");
        Challenge challenge2 = Manager.game().getChallengesMap().get("2");
        Challenge challenge3 = Manager.game().getChallengesMap().get("3");

        if (challenge1.isSolved()) {
            hint1.setVisibility(View.VISIBLE);
        } else {
            hint1.setVisibility(View.INVISIBLE);
        }

        if (challenge2.isSolved()) {
            hint2.setVisibility(View.VISIBLE);
        } else {
            hint2.setVisibility(View.INVISIBLE);
        }

        if (challenge3.isSolved()) {
            hint3.setVisibility(View.VISIBLE);
        } else {
            hint3.setVisibility(View.INVISIBLE);
        }

        if (challenge1.isSolved() && challenge2.isSolved() && challenge3.isSolved()) {
            solveButton.setEnabled(true);
            solveButton.setTextColor(Manager.activity().getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            solveButton.setEnabled(false);
            solveButton.setTextColor(Manager.activity().getResources().getColor(R.color.locked_challenge));
        }
    }
}
