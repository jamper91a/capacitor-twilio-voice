package com.jamper91.capacitor.twilio.voice.fcm;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jamper91.capacitor.twilio.voice.Constants;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.CancelledCallInvite;
import com.twilio.voice.MessageListener;
import com.twilio.voice.Voice;

public class VoiceFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "VoiceFCMService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "VoiceFirebaseMessagingService onCreate ");
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Received onMessageReceived()");
        Log.d(TAG, "Bundle data: " + remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            boolean valid = Voice.handleMessage(this, remoteMessage.getData(), new MessageListener() {
                @Override
                public void onCallInvite(@NonNull CallInvite callInvite) {
                    final int notificationId = (int) System.currentTimeMillis();
                    handleInvite(callInvite, notificationId);
                }

                @Override
                public void onCancelledCallInvite(@NonNull CancelledCallInvite cancelledCallInvite, @Nullable CallException callException) {
                    handleCanceledCallInvite(cancelledCallInvite);
                }
            });

            if (!valid) {
                Log.e(TAG, "The message was not a valid Twilio Voice SDK payload: " +
                        remoteMessage.getData());
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    private void handleInvite(CallInvite callInvite, int notificationId) {
        Log.i(TAG, "handleInvite");

        Intent intent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName())
                .setPackage(null)
                .putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
                .putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
                .setAction(Constants.ACTION_INCOMING_CALL)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    private void handleCanceledCallInvite(CancelledCallInvite cancelledCallInvite) {

        Intent intent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName())
                .setPackage(null)
                .setAction(Constants.ACTION_CANCEL_CALL)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }
}
