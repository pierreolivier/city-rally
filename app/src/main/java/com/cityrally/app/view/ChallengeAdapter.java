package com.cityrally.app.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.cityrally.app.R;
import com.cityrally.app.game.Game;
import com.cityrally.app.game.GameResultListener;
import com.cityrally.app.location.SimpleGeofence;
import com.cityrally.app.manager.Challenge;
import com.cityrally.app.manager.Manager;

import java.util.Collection;

/**
 * Created by po on 11/25/14.
 */
public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private Challenge[] mChallenges;
    private int lastPosition = -1;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mContainer;
        public ImageView mImageView;
        public TextView mTitleView;
        public TextView mSubTitleView;
        public TextView mTextView;
        public Button mExploreButton;
        public Button mSolveButton;
        public ImageView mSolvedView;

        public ViewHolder(View v) {
            super(v);

            mContainer = (LinearLayout) v.findViewById(R.id.card_relative_layout);
            mImageView = (ImageView) v.findViewById(R.id.info_image);
            mTitleView = (TextView) v.findViewById(R.id.info_title);
            mSubTitleView = (TextView) v.findViewById(R.id.info_subtile);
            mTextView = (TextView) v.findViewById(R.id.info_text);
            mExploreButton = (Button) v.findViewById(R.id.exploreButton);
            mSolveButton = (Button) v.findViewById(R.id.solveButton);
            mSolvedView = (ImageView) v.findViewById(R.id.info_solved);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChallengeAdapter(Collection<Challenge> challenges, Context context) {
        mChallenges = challenges.toArray(new Challenge[challenges.size()]);
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChallengeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_challenges, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        //TextView textView = (TextView) v.findViewById(R.id.info_text);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        final Challenge challenge = mChallenges[position];

        holder.mTitleView.setText(challenge.getTitle());
        holder.mSubTitleView.setText(challenge.getSubtitle());
        holder.mTextView.setText(challenge.getText());

        if (!challenge.isUnlocked()) {
            holder.mSolveButton.setEnabled(false);
            holder.mSolveButton.setTextColor(Manager.activity().getResources().getColor(R.color.locked_challenge));
        } else {
            holder.mSolveButton.setEnabled(true);
            holder.mSolveButton.setTextColor(Manager.activity().getResources().getColor(android.R.color.holo_orange_dark));
            holder.mImageView.setImageResource(challenge.getImage());
        }

        if (challenge.isSolved()) {
            holder.mSolvedView.setVisibility(View.VISIBLE);
        } else {
            holder.mSolvedView.setVisibility(View.INVISIBLE);
        }

        holder.mExploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (challenge.getId().equals("1")) {
                    Manager.location().startScenario1();
                }

                SimpleGeofence geofence = challenge.getGeofence();
                Manager.location().addMarker(challenge.getId(), Manager.activity().getText(challenge.getTitle()).toString(), geofence.getLatitude(), geofence.getLongitude());
                Manager.location().moveCamera(geofence.getLatitude(), geofence.getLongitude());

                Manager.activity().getNavigationDrawerFragment().selectItem(0);
            }
        });

        holder.mSolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(Manager.activity());
                dialog.setCancelable(false);
                dialog.setMessage(challenge.getTitle());
                dialog.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Game game = challenge.getGame();

                        if (game != null) {
                            game.start(new GameResultListener() {
                                @Override
                                public void onResult(boolean success) {
                                    if (success && !challenge.isSolved()) {
                                        challenge.setSolved(true);
                                        Manager.game().saveChallenges();

                                        Manager.activity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (ChallengesFragment.mAdapter != null) {
                                                    ChallengesFragment.mAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                    Log.e("result", "" + success);
                                }
                            });
                        }
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
        });

        setAnimation(holder.mContainer, position);
    }

    private void setAnimation(View viewToAnimate, int position)    {
        // If the bound view wasn't previously displayed on screen, it's animated

        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChallenges.length;
    }
}
