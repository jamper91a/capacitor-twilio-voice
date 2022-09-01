package com.jamper91.capacitor.twilio.voice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginHandle;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twilio.audioswitch.AudioSwitch;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@CapacitorPlugin(
        name = "TwilioVoice",
        permissions = {
                @Permission(
                        alias = "call",
                        strings = {
                                Manifest.permission.RECORD_AUDIO,
                        }
                ),
        }
)
public class TwilioVoicePlugin extends Plugin {

    private static final String TAG = "TwilioVoicePlugin";

    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private String accessToken = "PASTE_YOUR_ACCESS_TOKEN_HERE";

    /*
     * Audio device management
     */
    private AudioSwitch audioSwitch;
    private int savedVolumeControlStream;
    private MenuItem audioDeviceMenuItem;

    private boolean isReceiverRegistered = false;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;

    // Empty HashMap, never populated for the Quickstart
    HashMap<String, String> params = new HashMap<>();

    private static CallInvite activeCallInvite;
    private static Call activeCall;
    private static int activeCallNotificationId;

    RegistrationListener registrationListener = registrationListener();
    Call.Listener callListener = callListener();
    TwilioVoiceNotificationService twilioVoiceNotificationService;

    public static TwilioVoiceConfig twilioVoiceConfig;
    public static TwilioVoiceNotification twilioVoiceNotification;

    public static Bridge staticBridge = null;
    private static String fcmToken;
    @Override
    public void load() {
        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.twilioVoiceConfig = new TwilioVoiceConfig(getConfig(), getContext());
        this.twilioVoiceNotification = new TwilioVoiceNotification(this.twilioVoiceConfig, getContext());
        this.twilioVoiceNotificationService = new TwilioVoiceNotificationService();
        staticBridge = this.bridge;
        audioSwitch = new AudioSwitch(getActivity());
        savedVolumeControlStream = getActivity().getVolumeControlStream();
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    //region Capacitor

    /**
     * Will register a device with Twilio.
     * It will ask for the permissions required
     * @param call
     */
    @PluginMethod
    public void registerDevice(PluginCall call) {
        if (getPermissionState("call") != PermissionState.GRANTED) {
            requestPermissionForAlias("call", call, "callPermsCallback");
        } else {
            this.registerDeviceOnTwilio(call);
        }
    }

    /**
     * It will accept the active call
     * @param call
     */
    @PluginMethod
    public void acceptCall(PluginCall call) {
        activeCallInvite.accept(getContext(), callListener);
        call.resolve();
    }

    /**
     * It will disconnect from the active call
     * @param call
     */

    @PluginMethod
    public void disconnect(PluginCall call) {
        if (activeCall != null) {
            activeCall.disconnect();
            activeCall = null;
        }
        call.resolve();
    }

    /**
     * It will send digits to the active call
     * @param call
     */
    @PluginMethod
    public void sendDigits(PluginCall call) {

        String code = call.getString("code");
        if(activeCall != null) {
            activeCall.sendDigits(code);
        }
        call.resolve();
    }

    @PermissionCallback
    private void callPermsCallback(PluginCall call) {
        if (getPermissionState("call") == PermissionState.GRANTED) {
            this.registerDeviceOnTwilio(call);
        } else {
            call.reject("Permission is required to make a call");
        }
    }

    private static void notifyCapacitor(String eventName, JSObject data, boolean retainUntilConsumed){
        TwilioVoicePlugin plugin = TwilioVoicePlugin.getTwilioPluginInstance();
        if(plugin != null) {
            plugin.notifyListeners(eventName, data, retainUntilConsumed);
        }
    }

    //endregion

    //region Twilio Voice Android


    private void registerDeviceOnTwilio(PluginCall call){
        Log.i(TAG, "registerDeviceOnTwilio");
        String accessTokenP = call.getString("accessToken");
        this.accessToken =  accessTokenP;
        if(accessTokenP.isEmpty()) {
            call.reject("Access token no valid");
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        call.reject("No FCM Token");
                        return;
                    }
                    if (null != task.getResult()) {
                        String fcmToken = Objects.requireNonNull(task.getResult());
                        Voice.register(accessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
                        JSObject ret = new JSObject();
                        ret.put("value", "done");
                        call.resolve(ret);
                        return;
                    }
                    call.reject("No FCM Token");
                    return;

                });


    }

    private Call.Listener callListener() {
        return new Call.Listener() {
            /*
             * This callback is emitted once before the Call.Listener.onConnected() callback when
             * the callee is being alerted of a Call. The behavior of this callback is determined by
             * the answerOnBridge flag provided in the Dial verb of your TwiML application
             * associated with this client. If the answerOnBridge flag is false, which is the
             * default, the Call.Listener.onConnected() callback will be emitted immediately after
             * Call.Listener.onRinging(). If the answerOnBridge flag is true, this will cause the
             * call to emit the onConnected callback only after the call is answered.
             * See answeronbridge for more details on how to use it with the Dial TwiML verb. If the
             * twiML response contains a Say verb, then the call will emit the
             * Call.Listener.onConnected callback immediately after Call.Listener.onRinging() is
             * raised, irrespective of the value of answerOnBridge being set to true or false
             */
            @Override
            public void onRinging(@NonNull Call call) {
                Log.d(TAG, "Ringing");

            }

            @Override
            public void onConnectFailure(@NonNull Call call, @NonNull CallException error) {

                Log.d(TAG, "Connect failure");
                String message = String.format(
                        Locale.US,
                        "Call Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
            }

            @Override
            public void onConnected(@NonNull Call call) {
//                audioSwitch.activate();
                Log.d(TAG, "Connected");
                activeCall = call;
            }

            @Override
            public void onReconnecting(@NonNull Call call, @NonNull CallException callException) {
                Log.d(TAG, "onReconnecting");
            }

            @Override
            public void onReconnected(@NonNull Call call) {
                Log.d(TAG, "onReconnected");
            }

            @Override
            public void onDisconnected(@NonNull Call call, CallException error) {
//                audioSwitch.deactivate();

                Log.d(TAG, "Disconnected");
                if (error != null) {
                    String message = String.format(
                            Locale.US,
                            "Call Error: %d, %s",
                            error.getErrorCode(),
                            error.getMessage());
                    Log.e(TAG, message);
                }
            }
            /*
             * currentWarnings: existing quality warnings that have not been cleared yet
             * previousWarnings: last set of warnings prior to receiving this callback
             *
             * Example:
             *   - currentWarnings: { A, B }
             *   - previousWarnings: { B, C }
             *
             * Newly raised warnings = currentWarnings - intersection = { A }
             * Newly cleared warnings = previousWarnings - intersection = { C }
             */
            public void onCallQualityWarningsChanged(@NonNull Call call,
                                                     @NonNull Set<Call.CallQualityWarning> currentWarnings,
                                                     @NonNull Set<Call.CallQualityWarning> previousWarnings) {

                if (previousWarnings.size() > 1) {
                    Set<Call.CallQualityWarning> intersection = new HashSet<>(currentWarnings);
                    currentWarnings.removeAll(previousWarnings);
                    intersection.retainAll(previousWarnings);
                    previousWarnings.removeAll(intersection);
                }

                String message = String.format(
                        Locale.US,
                        "Newly raised warnings: " + currentWarnings + " Clear warnings " + previousWarnings);
                Log.e(TAG, message);
            }
        };
    }

    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
                Log.d(TAG, "Successfully registered FCM " + fcmToken);
            }

            @Override
            public void onError(@NonNull RegistrationException error,
                                @NonNull String accessToken,
                                @NonNull String fcmToken) {
                String message = String.format(
                        Locale.US,
                        "Registration Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
            }
        };
    }

    public CallInvite getActiveCallInvite() {
        return activeCallInvite;
    }

    public void setActiveCallInvite(CallInvite activeCallInvite) {
        this.activeCallInvite = activeCallInvite;
    }

    @Override
    protected void handleOnNewIntent(Intent data) {
        super.handleOnNewIntent(data);
        handleIncomingCallIntent(data);

    }
    public static void handleIncomingCallIntent(Intent intent) {
        Log.i(TAG, "handleIncomingCallIntent");
//        if (intent != null && intent.getAction() != null) {
//            String action = intent.getAction();
//            activeCallInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
//            activeCallNotificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);
//            JSObject ret = new JSObject();
//            switch (action) {
//                case Constants.ACTION_INCOMING_CALL:
//                    notifyCapacitor("incomingCall", ret, false);
//                    break;
//                case Constants.ACTION_INCOMING_CALL_NOTIFICATION:
//                    notifyCapacitor("incomingCall", ret, false);
//                    break;
//                case Constants.ACTION_CANCEL_CALL:
//                    notifyCapacitor("callCancel", ret, false);
//                    break;
//                case Constants.ACTION_FCM_TOKEN:
//                    registerForCallInvites();
//                    break;
//                case Constants.ACTION_ACCEPT:
////                    answer();
//                    notifyCapacitor("callAccepted", ret, false);
//                    break;
//                default:
//                    break;
//            }
//        }
    }


    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "VoiceBroadcastReceiver onReceive");
            String action = intent.getAction();
            if (action != null && (action.equals(Constants.ACTION_INCOMING_CALL) || action.equals(Constants.ACTION_CANCEL_CALL))) {
                /*
                 * Handle the incoming or cancelled call invite
                 */
                activeCallInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
                activeCallNotificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);
                handleIncomingCallIntent(intent);
            }
        }

    }

    //endregion

    public static TwilioVoicePlugin getTwilioPluginInstance() {
        if (staticBridge != null && staticBridge.getWebView() != null) {
            PluginHandle handle = staticBridge.getPlugin("TwilioVoice");
            if (handle == null) {
                return null;
            }
            return (TwilioVoicePlugin) handle.getInstance();
        }
        return null;
    }


    //region Notifications

    public static void handleIncomingCall(CallInvite callInvite, int notificationId){
        JSObject ret = new JSObject();
        activeCallInvite = callInvite;
        notifyCapacitor("incomingCall", ret, false);
        twilioVoiceNotification.handleIncomingCall(callInvite, notificationId);
    }

    public static void callAccepted(CallInvite callInvite, int notificationId){
        JSObject ret = new JSObject();
        notifyCapacitor("callAccepted", ret, false);
    }

    public static void callRejected(CallInvite callInvite){
        JSObject ret = new JSObject();
        callInvite.reject(getTwilioPluginInstance().getContext());
        notifyCapacitor("callRejected", ret, false);
    }

    public static void callCanceled(){
        JSObject ret = new JSObject();
        notifyCapacitor("callCanceled", ret, false);
    }


    //endregion
}
