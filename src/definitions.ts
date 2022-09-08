import {PluginListenerHandle} from "@capacitor/core";
export interface PluginsConfig {
  TwilioVoice?: {
    /**
     * Text to show on the incoming call notification
     *
     * @example "Someone is calling you"
     */
    notificationText?:  string;

    /**
     * Title of the notification for an incoming call.
     *
     * @default Twilio
     * @example "#FF9900"
     */
    notificationTitle?: string;

    /**
     * Name of the icon on the resource folder to be shown in the notification
     *Just Android
     * @default Twilio
     * @example "#FF9900"
     */
    androidNotificationIcon?: string;

    /**
     * Name of the icon on the resource folder to be shown in the accept action in the notification
     * Just Android
     */
    androidNotificationAcceptIcon?: string;
    /**
     * Name of the icon on the resource folder to be shown in the decline action in the notification
     * Just Android
     */
    androidNotificationRejectIcon?: string;
  };
}

export interface TwilioVoicePlugin {
  registerDevice(option: TwilioVoiceOptions): Promise<void>;
  acceptCall(): Promise<void>;
  disconnect(): Promise<void>;
  sendDigits(options: SendDigitsOptions): Promise<void>
  /**
   * Listens
   */
  addListener(
      eventName: 'callAccepted',
      listenerFunc: () => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Listens
   */
  addListener(
      eventName: 'callRejected',
      listenerFunc: () => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
  /**
   * Listens
   */
  addListener(
      eventName: 'callCanceled',
      listenerFunc: () => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  addListener(
      eventName: 'incomingCall',
      listenerFunc: () => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Removes all listeners
   */
  removeAllListeners(): Promise<void>;
}
export interface TwilioVoiceOptions {
  accessToken: string;
}

export interface SendDigitsOptions {
  code: string;
}
