package com.amigo.todo;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by sudhanshu.gupta on 10/01/16.
 */
public class ToDoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(getApplicationContext(), NotificationService.class);

        //Create a pending intent and get access to the alarm manager
        PendingIntent pIntent =
                PendingIntent.getService(getApplicationContext(), 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //get access to the alarm manager
        AlarmManager alarmManager =
                (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 7); // For 7 AM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);

    }
}
