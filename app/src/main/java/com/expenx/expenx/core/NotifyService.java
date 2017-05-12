package com.expenx.expenx.core;

import com.expenx.expenx.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.expenx.expenx.activity.ExpenxActivity;
import com.expenx.expenx.activity.ReminderActivity;

/**
 * Created by Imanshu on 5/10/2017.
 */

public class NotifyService extends Service{

    NotificationManager manager;
    Notification myNotication;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        /*
        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.circle_login_activity, "Expenx Reminder", System.currentTimeMillis());
        Intent myIntent = new Intent(this , ExpenxActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, myIntent, 0);
        notification.setLatestEventInfo(this, "Notify label", "Notify text", contentIntent);
        mNM.notify(NOTIFICATION, notification); */


        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");

        PendingIntent pendingIntent = PendingIntent.getActivity(NotifyService.this, 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(NotifyService.this);

        builder.setAutoCancel(false);
        builder.setTicker("ExpenX");
        builder.setContentTitle("ExpenX Notification");
        builder.setContentText("Reminder");
        builder.setSmallIcon(R.drawable.ic_reminder);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("Mange your daily incomes and expenses");   //API level 16
        builder.setNumber(100);
        builder.build();

        myNotication = builder.getNotification();
        manager.notify(11, myNotication);

    }

}
