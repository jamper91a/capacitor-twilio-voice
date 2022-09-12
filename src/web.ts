import { WebPlugin } from '@capacitor/core';

import type {TwilioVoiceOptions, TwilioVoicePlugin} from './definitions';
import {SendDigitsOptions} from "./definitions";

export class TwilioVoiceWeb extends WebPlugin implements TwilioVoicePlugin {


  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  registerDevice(option: TwilioVoiceOptions): Promise<void> {
    console.log('registerDevice');
    return Promise.resolve(undefined);

  }

  acceptCall(): Promise<void> {
    return Promise.resolve(undefined);
  }
  disconnect(): Promise<void> {
    return Promise.resolve(undefined);
  }
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  sendDigits(options: SendDigitsOptions): Promise<void> {
    return Promise.resolve(undefined);
  }

  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  async checkPermissions(): Promise<PermissionStatus> {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return Promise.resolve(undefined);
  }

  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  async requestPermissions(): Promise<PermissionStatus> {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return Promise.resolve(undefined);
  }
}
