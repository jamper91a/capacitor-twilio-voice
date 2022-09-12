import type { PermissionState, PluginListenerHandle } from '@capacitor/core';

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

  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
}
export interface TwilioVoiceOptions {
  accessToken: string;
}

export interface SendDigitsOptions {
  code: string;
}

export interface PermissionStatus {
  // TODO: change 'location' to the actual name of your alias!
  call: PermissionState;
}
