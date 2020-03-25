package com.tsa.EventMe;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "remindMe")
                .setSmallIcon(R.drawable.baseline_account_circle_24)
                .setContentTitle("Event remind")
                .setContentText(intent.getExtras().getString("Topic"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        manager.notify(101, builder.build());

    }
}
