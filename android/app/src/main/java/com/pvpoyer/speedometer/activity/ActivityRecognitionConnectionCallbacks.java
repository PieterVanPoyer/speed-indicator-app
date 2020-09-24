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

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
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
