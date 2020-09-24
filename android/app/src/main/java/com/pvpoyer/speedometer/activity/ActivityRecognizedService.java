package com.pvpoyer.speedometer.activity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognizedService extends IntentService {

    public static final String NOT_STANDING_STILL = "com.pvpoyer.speedometer.activity.notstandingstill";
    public static final String STANDING_STILL = "com.pvpoyer.speedometer.activity.standingstill";

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("ActivityRecogition", "handleIntent: ");
        if (ActivityTransitionResult.hasResult(intent)) {
            Log.e("ActivityRecogition", "handleIntent: ActivityRecognitionResult");
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            handleDetectedActivities(result.getTransitionEvents());
        }
    }

    private void handleDetectedActivities(List<ActivityTransitionEvent> probableActivities) {
        for (ActivityTransitionEvent activity : probableActivities) {
            switch (activity.getActivityType()) {
                case DetectedActivity.IN_VEHICLE: {
                    if(ActivityTransition.ACTIVITY_TRANSITION_ENTER == activity.getTransitionType()) {
                        publishNotStandingStillResult();
                    }
                    Log.e("ActivityRecogition", "In Vehicle: ");
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    if(ActivityTransition.ACTIVITY_TRANSITION_ENTER == activity.getTransitionType()) {
                        publishNotStandingStillResult();
                    }
                    Log.e("ActivityRecogition", "On Bicycle: ");
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    if(ActivityTransition.ACTIVITY_TRANSITION_ENTER == activity.getTransitionType()) {
                        publishNotStandingStillResult();
                    }
                    Log.e("ActivityRecogition", "On Foot: ");
                    break;
                }
                case DetectedActivity.RUNNING: {
                    if(ActivityTransition.ACTIVITY_TRANSITION_ENTER == activity.getTransitionType()) {
                        publishNotStandingStillResult();
                    }
                    Log.e("ActivityRecogition", "Running: ");
                    break;
                }
                case DetectedActivity.STILL: {
                    if(ActivityTransition.ACTIVITY_TRANSITION_ENTER == activity.getTransitionType()) {
                        publishStandingStillResult();
                    } else {
                        publishNotStandingStillResult();
                    }
                    Log.e("ActivityRecogition", "Still: ");

                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e("ActivityRecogition", "Tilting: ");
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e("ActivityRecogition", "Walking: ");
                    if(ActivityTransition.ACTIVITY_TRANSITION_ENTER == activity.getTransitionType()) {
                        publishNotStandingStillResult();
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e("ActivityRecogition", "Unknown: ");
                    if(ActivityTransition.ACTIVITY_TRANSITION_ENTER == activity.getTransitionType()) {
                        publishNotStandingStillResult();
                    }
                    break;
                }
            }
        }
    }

    private void publishNotStandingStillResult() {
        Intent intent = new Intent(NOT_STANDING_STILL);
        sendBroadcast(intent);
    }

    private void publishStandingStillResult() {
        Intent intent = new Intent(STANDING_STILL);
        sendBroadcast(intent);
    }

}