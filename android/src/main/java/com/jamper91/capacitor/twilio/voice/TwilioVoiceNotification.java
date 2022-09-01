package com.jamper91.capacitor.twilio.voice;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.twilio.voice.CallInvite;

public class TwilioVoiceNotification {

    private static final String TAG = "TwilioVoiceNotification";
    private TwilioVoiceConfig config;
    private Context context;

    public TwilioVoiceNotification(TwilioVoiceConfig config, Context context) {
        this.config = config;
        this.context = context;
        this.createChannel(NotificationManager.IMPORTANCE_HIGH);
    }

    public void handleIncomingCall(CallInvite callInvite, int notificationId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setCallInProgressNotification(callInvite, notificationId);
        }
        sendCallInviteToActivity(callInvite, notificationId);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void setCallInProgressNotification(CallInvite callInvite, int notificationId) {
//        Log.i(TAG, "setCallInProgressNotification NOTIFICATION not shown");
        if (isAppVisible()) {
            Log.i(TAG, "setCallInProgressNotification - app is visible.");
//            startForeground(notificationId, createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_LOW));
//            createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_LOW);
        } else {
            Log.i(TAG, "setCallInProgressNotification - app is NOT visible.");
//            startForeground(notificationId, createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_HIGH));
            createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_HIGH);
        }
    }

    private void sendCallInviteToActivity(CallInvite callInvite, int notificationId) {
        Log.i(TAG, "sendCallInviteToActivity sHOULD DO NOTHING");
//        if (Build.VERSION.SDK_INT >= 29 && !isAppVisible()) {
//            return;
//        }
//        Intent intent = new Intent(context, TwilioVoicePlugin.class);
//        intent.setAction(Constants.ACTION_INCOMING_CALL);
//        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
//        intent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        TwilioVoicePlugin.handleIncomingCallIntent(intent);
//        TwilioVoicePlugin.handleIncomingCall(callInvite, notificationId);
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner
                .get()
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.STARTED);
    }


    private Notification createNotification(CallInvite callInvite, int notificationId, int channelImportance) {

        Intent intent = new Intent(context, TwilioVoicePlugin.class);
        intent.setAction(Constants.ACTION_INCOMING_CALL_NOTIFICATION);
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
        /*
         * Pass the notification id and call sid to use as an identifier to cancel the
         * notification later
         */
        Bundle extras = new Bundle();
        extras.putString(Constants.CALL_SID_KEY, callInvite.getCallSid());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return buildNotification(this.config.getNotificationText(),
                    pendingIntent,
                    extras,
                    callInvite,
                    notificationId,
                    createChannel(channelImportance));
        } else {
            //noinspection deprecation
            return new NotificationCompat.Builder(context)
                    .setSmallIcon(this.config.getNotificationIcon())
                    .setContentTitle(this.config.getNotificationTitle())
                    .setContentText(this.config.getNotificationText())
                    .setAutoCancel(true)
                    .setExtras(extras)
                    .setContentIntent(pendingIntent)
                    .setGroup("test_app_notification")
                    .setCategory(Notification.CATEGORY_CALL)
                    .setColor(Color.rgb(214, 10, 37)).build();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private Notification buildNotification(String text, PendingIntent pendingIntent, Bundle extras,
                                           final CallInvite callInvite,
                                           int notificationId,
                                           String channelId) {
        Intent rejectIntent = new Intent(context, TwilioVoiceNotificationService.class);
        rejectIntent.setAction(Constants.ACTION_REJECT);
        rejectIntent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        rejectIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        PendingIntent piRejectIntent = PendingIntent.getService(context, notificationId, rejectIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent acceptIntent = new Intent(context, TwilioVoicePlugin.class);
        acceptIntent.setAction(Constants.ACTION_ACCEPT);
        acceptIntent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent piAcceptIntent = PendingIntent.getActivity(context, notificationId, acceptIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder =
                new Notification.Builder(context, channelId)
                        .setSmallIcon(this.config.getNotificationIcon())
                        .setContentTitle(this.config.getNotificationTitle())
                        .setContentText(this.config.getNotificationText())
                        .setCategory(Notification.CATEGORY_CALL)
                        .setExtras(extras)
                        .setAutoCancel(true)
                        .addAction(this.config.getNotificationRejectIcon(), "Decline", piRejectIntent)
                        .addAction(this.config.getNotificationAcceptIcon(), "Accept", piAcceptIntent)
                        .setFullScreenIntent(pendingIntent, true);

        return builder.build();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String createChannel(int channelImportance) {
        NotificationChannel callInviteChannel = new NotificationChannel(Constants.VOICE_CHANNEL_HIGH_IMPORTANCE,
                "Primary Voice Channel", NotificationManager.IMPORTANCE_HIGH);
        String channelId = Constants.VOICE_CHANNEL_HIGH_IMPORTANCE;

        if (channelImportance == NotificationManager.IMPORTANCE_LOW) {
            callInviteChannel = new NotificationChannel(Constants.VOICE_CHANNEL_LOW_IMPORTANCE,
                    "Primary Voice Channel", NotificationManager.IMPORTANCE_LOW);
            channelId = Constants.VOICE_CHANNEL_LOW_IMPORTANCE;
        }
        callInviteChannel.setLightColor(Color.GREEN);
        callInviteChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(callInviteChannel);

        return channelId;
    }
}
