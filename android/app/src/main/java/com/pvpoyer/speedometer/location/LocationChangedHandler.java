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

public class LocationChangedHandler extends com.google.android.gms.location.LocationCallback {
    private MainActivity mainActivity;

    public LocationChangedHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            return;
        }

        Location location = locationResult.getLastLocation();

        float speedInMeterPerSeconde = location.getSpeed();

        // accuracy check...
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
                        // return;
                    }
                } else {
                    // if the speed accuracy is more than 50% wrong of the effective speed, we ignore the speed
                    if (Math.abs(location.getSpeedAccuracyMetersPerSecond()) > speedInMeterPerSeconde / 2) {
                        Log.i("MainActivityAccCheck", "speed accuracy check, not accurate enough above 1 speed" + speedInMeterPerSeconde + " accuracy was: " + location.getSpeedAccuracyMetersPerSecond());
                        myCurrentAccuracyTextView.setTextColor(mainActivity.getColor(R.color.colorAccuracyTooLow));
                        // return;
                    }
                }
            } else {
                myCurrentAccuracyTextView.setTextColor(mainActivity.getColor(R.color.colorAccuracyTooLow));
            }
        }

        double speedInKilometersPerHour = 0.0;
        if (location.getSpeed() != 0.0f) {
            speedInKilometersPerHour = BigDecimal.valueOf(speedInMeterPerSeconde).divide(new BigDecimal("1000")).multiply(new BigDecimal("3600")).doubleValue();
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
}
