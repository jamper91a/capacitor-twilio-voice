package com.jamper91.capacitor.twilio.voice;

import android.content.Context;

import com.getcapacitor.PluginConfig;

public class TwilioVoiceConfig {
    private PluginConfig pluginConfig;
    private Context context;
    private String notificationText = "notificationText";
    private String notificationTitle = "notificationTitle";
    private String notificationIcon = "notificationIcon";
    private String notificationAcceptIcon = "notificationAcceptIcon";
    private String notificationRejectIcon = "notificationRejectIcon";

    public TwilioVoiceConfig(PluginConfig pluginConfig, Context context) {
        pluginConfig = pluginConfig;
        if(this.pluginConfig != null) {
            this.notificationText = pluginConfig.getString("notificationText");
            this.notificationTitle = pluginConfig.getString("notificationTitle");
            this.notificationIcon = pluginConfig.getString("androidNotificationIcon");
            this.notificationAcceptIcon = pluginConfig.getString("androidNotificationAcceptIcon");
            this.notificationRejectIcon = pluginConfig.getString("androidNotificationRejectIcon");
        }

    }


    public String getNotificationText() {
        return notificationText;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public int getNotificationIcon() {
        int drawableId = context.getResources().getIdentifier(notificationIcon, "drawable", context.getPackageName());
        if(drawableId == 0) {
            return R.drawable.ic_call_white_24dp;
        }
        return drawableId;
    }

    public int getNotificationAcceptIcon() {
        int drawableId = context.getResources().getIdentifier(notificationAcceptIcon, "drawable", context.getPackageName());
        if(drawableId == 0) {
            return R.drawable.ic_call_black_24dp;
        }
        return drawableId;
    }

    public int getNotificationRejectIcon() {
        int drawableId = context.getResources().getIdentifier(notificationRejectIcon, "drawable", context.getPackageName());
        if(drawableId == 0) {
            return R.drawable.ic_call_end_white_24dp;
        }
        return drawableId;
    }
}
