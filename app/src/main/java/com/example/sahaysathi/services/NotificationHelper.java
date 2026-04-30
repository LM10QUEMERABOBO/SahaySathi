package com.example.sahaysathi.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sahaysathi.InitActivity;
import com.example.sahaysathi.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "sahaysathi_notifications";
    private static final String CHANNEL_NAME = "SahaySathi Notifications";
    private static final String CHANNEL_DESC = "Notifications for registration, event requests, applications and status updates";

    public static final int NOTIFICATION_REGISTRATION = 101;
    public static final int NOTIFICATION_POST_REQUEST = 102;
    public static final int NOTIFICATION_APPLIED_EVENT = 103;
    public static final int NOTIFICATION_STATUS = 104;
    public static final int NOTIFICATION_GENERAL = 105;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showNotification(
            Context context,
            int notificationId,
            String title,
            String message
    ) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, InitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        manager.notify(notificationId, builder.build());
    }

    public static void registrationSuccess(Context context, String userName) {
        showNotification(
                context,
                NOTIFICATION_REGISTRATION,
                "Registration Successful",
                "Welcome " + userName + "! Your SahaySathi account has been created successfully."
        );
    }

    public static void postRequestSuccess(Context context, String eventName) {
        showNotification(
                context,
                NOTIFICATION_POST_REQUEST,
                "Request Posted Successfully",
                "Your volunteer request for \"" + eventName + "\" has been posted successfully."
        );
    }

    public static void appliedEventSuccess(Context context, String eventName) {
        showNotification(
                context,
                NOTIFICATION_APPLIED_EVENT,
                "Application Submitted",
                "You have successfully applied for \"" + eventName + "\"."
        );
    }

    public static void applicantAccepted(Context context, String eventName) {
        showNotification(
                context,
                NOTIFICATION_STATUS,
                "Application Accepted",
                "Congratulations! Your application for \"" + eventName + "\" has been accepted."
        );
    }

    public static void applicantRejected(Context context, String eventName) {
        showNotification(
                context,
                NOTIFICATION_STATUS,
                "Application Rejected",
                "Your application for \"" + eventName + "\" was not accepted this time."
        );
    }
}