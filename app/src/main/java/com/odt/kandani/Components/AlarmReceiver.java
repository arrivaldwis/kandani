package com.odt.kandani.Components;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.odt.kandani.R;
import com.odt.kandani.Database.Reminder;
import com.odt.kandani.Database.ReminderDatabase;
import com.odt.kandani.View.ReminderEditActivity;

import java.util.Calendar;


public class AlarmReceiver extends WakefulBroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int mReceivedID = Integer.parseInt(intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID));

        ReminderDatabase rb = new ReminderDatabase(context);
        Reminder reminder = rb.getReminder(mReceivedID);
        String mTitle = reminder.getTitle();

        Intent editIntent = new Intent(context, ReminderEditActivity.class);
        editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_alarm_on_white_24dp)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setTicker(mTitle)
                .setContentText(mTitle)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(mClick)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(mReceivedID, mBuilder.build());
    }

    public void setAlarm(Context context, Calendar calendar, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void setRepeatAlarm(Context context, Calendar calendar, int ID, long RepeatTime) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                RepeatTime , mPendingIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}