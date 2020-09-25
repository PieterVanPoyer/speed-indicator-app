package com.pvpoyer.speedometer.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Only the activity changes are detected. I did not found an easy way to detect the currect activity without the transition.
 * So when the activity is destroyed, we must wait untill a transition (from STILL to WALKING or something like that) occurs, before acting on it.
 *
 * We always starts with the assumption that the user is active. (Strange, because mostly the user is not active when starting the app.
 *      But there is no easy way to know for now)
 */
public class ActivityRecognitionConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

    private Activity activity;

    public ActivityRecognitionConnectionCallbacks(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this.activity, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this.activity, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        Task<Void> task = ActivityRecognition.getClient(this.activity)
                .requestActivityTransitionUpdates(request, pendingIntent);

        task.addOnSuccessListener(
                result -> {
                    // Handle success
                    Log.i("ActivityRecogition", "onSuccess: ");
                }
        );

        task.addOnFailureListener(
                e -> Log.e("ActivityRecogition", "Cannot connect to the transition updates")
        );

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
