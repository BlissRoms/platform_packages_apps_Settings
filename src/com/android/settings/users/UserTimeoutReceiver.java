/*
 * Copyright (C) 2023 The Calyx Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.users;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;

import com.android.settings.R;

import java.util.Calendar;

import android.provider.Settings;

public class UserTimeoutReceiver extends BroadcastReceiver {

    private static final String ACTION_USER_TIMEOUT = "android.intent.action.USER_TIMEOUT";
    private static final String USER_TIMEOUT_CHANNEL = "user_timeout_channel";
    private static final String EXTRA_NOTIFICATION = "notification";

    private AlarmManager mAlarmManager;

    private PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        UserManager userManager = UserManager.get(context);
        if (intent != null) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                mAlarmManager = context.getSystemService(AlarmManager.class);
                mPendingIntent = PendingIntent.getBroadcast(context, context.getUserId(),
                        new Intent(ACTION_USER_TIMEOUT)
                                .setPackage(context.getPackageName()),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                context.getContentResolver().registerContentObserver(
                        Settings.Secure.getUriFor(
                                Settings.Secure.USER_ACTIVITY_END_TIME), false,
                        new ContentObserver(new Handler(Looper.getMainLooper())) {
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                setTimeout(context);
                            }
                        });
                setTimeout(context);
            } else if (ACTION_USER_TIMEOUT.equals(intent.getAction())) {
                if (!intent.getBooleanExtra(EXTRA_NOTIFICATION, false)) {
                    NotificationManager notificationManager = NotificationManager.from(
                            context);
                    NotificationChannel notificationChannel = new NotificationChannel(
                            USER_TIMEOUT_CHANNEL,
                            context.getString(R.string.work_hours_title),
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(notificationChannel);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                            context.getUserId(),
                            new Intent(ACTION_USER_TIMEOUT).setPackage(
                                    context.getPackageName()).putExtra(
                                    EXTRA_NOTIFICATION, true),
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    Notification notification = new Notification.Builder(context,
                            USER_TIMEOUT_CHANNEL)
                            .setSmallIcon(R.drawable.ic_settings_multiuser)
                            .setContentTitle(
                                    context.getString(R.string.work_hours_end_notification_title))
                            .setContentText(
                                    context.getString(
                                            R.string.work_hours_end_notification_description))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    notificationManager.notify(R.drawable.ic_settings_multiuser, notification);
                } else if (userManager.isManagedProfile()) {
                    userManager.requestQuietModeEnabled(true, context.getUser());
                } else {
                    context.getSystemService(ActivityManager.class).stopUser(context.getUserId(),
                            true);
                }
            }
        }
    }

    private void setTimeout(Context context) {
        long time = Settings.Secure.getLong(context.getContentResolver(),
                Settings.Secure.USER_ACTIVITY_END_TIME, 0);
        if (time != 0) {
            Calendar current = Calendar.getInstance();
            Calendar timeout = Calendar.getInstance();
            timeout.setTimeInMillis(time);
            timeout.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH));
            if (current.compareTo(timeout) > 0) {
                timeout.add(Calendar.DATE, 1);
            }
            time = timeout.getTimeInMillis();
            mAlarmManager.set(
                    AlarmManager.RTC_WAKEUP, time, AlarmManager.WINDOW_EXACT,
                    AlarmManager.INTERVAL_DAY, mPendingIntent, null);
        } else {
            mAlarmManager.cancel(mPendingIntent);
        }
    }
}
