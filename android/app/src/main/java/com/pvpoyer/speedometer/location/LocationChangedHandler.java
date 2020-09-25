package com.pvpoyer.speedometer.location;

import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.LocationResult;
import com.pvpoyer.speedometer.MainActivity;
import com.pvpoyer.speedometer.R;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class LocationChangedHandler extends com.google.android.gms.location.LocationCallback {
    private MainActivity mainActivity;
    private boolean userIsActive = true; // defaults to true, we wait for a transition to know the activity of the user.

    public LocationChangedHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            return;
        }

        if(this.userIsActive) {
            handleLocationResultForActiveUser(locationResult);
        }
    }

    private void handleLocationResultForActiveUser(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();

        float speedInMeterPerSeconde = location.getSpeed();

        // accuracy check... => for now we only make the accuracy text red or black depending of the accuracy, we do not really take
        // action to drop certain locations, because in the field, it looks quite okay.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TextView myCurrentAccuracyTextView = this.mainActivity.findViewById(R.id.my_current_accuracy);
            myCurrentAccuracyTextView.setText(String.valueOf(location.getSpeedAccuracyMetersPerSecond()));
            myCurrentAccuracyTextView.setTextColor(mainActivity.getColor(R.color.colorAccuracyDefault));

            if (location.hasSpeedAccuracy()) {
                Log.i("MainActivityAccCheck", "speed accuracy check" + location.getSpeedAccuracyMetersPerSecond());

                if (speedInMeterPerSeconde < 1.0f) {
                    // if the speed accuracy is more than 100% wrong of the effective speed, we ignore the speed
                    if (Math.abs(location.getSpeedAccuracyMetersPerSecond()) > 1.0f) {
                        Log.i("MainActivityAccCheck", "speed accuracy check, not accurate enough below 1 speed" + speedInMeterPerSeconde + " accuracy was: " + location.getSpeedAccuracyMetersPerSecond());
                        myCurrentAccuracyTextView.setTextColor(mainActivity.getColor(R.color.colorAccuracyTooLow));
                    }
                } else {
                    // if the speed accuracy is more than 50% wrong of the effective speed, we ignore the speed
                    if (Math.abs(location.getSpeedAccuracyMetersPerSecond()) > speedInMeterPerSeconde / 2) {
                        Log.i("MainActivityAccCheck", "speed accuracy check, not accurate enough above 1 speed" + speedInMeterPerSeconde + " accuracy was: " + location.getSpeedAccuracyMetersPerSecond());
                        myCurrentAccuracyTextView.setTextColor(mainActivity.getColor(R.color.colorAccuracyTooLow));
                    }
                }
            } else {
                myCurrentAccuracyTextView.setTextColor(mainActivity.getColor(R.color.colorAccuracyTooLow));
            }
        }

        double speedInKilometersPerHour = 0.0;
        if (location.getSpeed() != 0.0f) {
            // 1 m/s is 3.6 km/h.
            // m/s to km/h is (speed / 1000) * 3600
            // So a precision of 6 for the division by 1000, is more than enough to be represented with one decimal as display.
            speedInKilometersPerHour = BigDecimal.valueOf(speedInMeterPerSeconde).divide(new BigDecimal("1000"),
                    new MathContext(6, RoundingMode.HALF_UP)).multiply(new BigDecimal("3600")).doubleValue();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("MainActivity", "getting locations! Speed: " + speedInKilometersPerHour + " accurate " + location.getSpeedAccuracyMetersPerSecond());
        } else {
            Log.i("MainActivity", "getting locations! Speed: " + speedInKilometersPerHour + " no accurate ");
        }

        TextView myCurrentSpeedTextView = this.mainActivity.findViewById(R.id.my_current_speed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NumberFormat instance = DecimalFormat.getInstance();
            instance.setMaximumFractionDigits(1);
            instance.setMinimumFractionDigits(1);
            String formatSpeed = instance.format(speedInKilometersPerHour);
            myCurrentSpeedTextView.setText(formatSpeed);
        } else {
            myCurrentSpeedTextView.setText(String.valueOf(speedInKilometersPerHour));
        }
    }

    public void userIsActive() {
        this.userIsActive = true;
    }

    public void userIsStandingStill() {
        this.userIsActive = false;
        this.setSpeedToZeroAsUserIsNotActive();
    }

    private void setSpeedToZeroAsUserIsNotActive() {
        TextView myCurrentSpeedTextView = this.mainActivity.findViewById(R.id.my_current_speed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NumberFormat instance = DecimalFormat.getInstance();
            instance.setMaximumFractionDigits(1);
            instance.setMinimumFractionDigits(1);
            String formatSpeed = instance.format(0);
            myCurrentSpeedTextView.setText(formatSpeed);
        } else {
            myCurrentSpeedTextView.setText(0);
        }
    }
}
