# capacitor-twilio-voice

Implementation of twilio voice Android and IOS sdk for capacitor

## Install

```bash
npm install capacitor-twilio-voice
npx cap sync
```
## Test Locally
```bash
npm run build
npm pack
```
## API

<docgen-index>

* [`registerDevice(...)`](#registerdevice)
* [`acceptCall()`](#acceptcall)
* [`disconnect()`](#disconnect)
* [`sendDigits(...)`](#senddigits)
* [`addListener('callAccepted', ...)`](#addlistenercallaccepted)
* [`addListener('callRejected', ...)`](#addlistenercallrejected)
* [`addListener('callCanceled', ...)`](#addlistenercallcanceled)
* [`addListener('incomingCall', ...)`](#addlistenerincomingcall)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### registerDevice(...)

```typescript
registerDevice(option: TwilioVoiceOptions) => Promise<void>
```

| Param        | Type                                                              |
| ------------ | ----------------------------------------------------------------- |
| **`option`** | <code><a href="#twiliovoiceoptions">TwilioVoiceOptions</a></code> |

--------------------


### acceptCall()

```typescript
acceptCall() => Promise<void>
```

--------------------


### disconnect()

```typescript
disconnect() => Promise<void>
```

--------------------


### sendDigits(...)

```typescript
sendDigits(options: SendDigitsOptions) => Promise<void>
```

| Param         | Type                                                            |
| ------------- | --------------------------------------------------------------- |
| **`options`** | <code><a href="#senddigitsoptions">SendDigitsOptions</a></code> |

--------------------


### addListener('callAccepted', ...)

```typescript
addListener(eventName: 'callAccepted', listenerFunc: () => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listens

| Param              | Type                        |
| ------------------ | --------------------------- |
| **`eventName`**    | <code>'callAccepted'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>  |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('callRejected', ...)

```typescript
addListener(eventName: 'callRejected', listenerFunc: () => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listens

| Param              | Type                        |
| ------------------ | --------------------------- |
| **`eventName`**    | <code>'callRejected'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>  |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('callCanceled', ...)

```typescript
addListener(eventName: 'callCanceled', listenerFunc: () => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listens

| Param              | Type                        |
| ------------------ | --------------------------- |
| **`eventName`**    | <code>'callCanceled'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>  |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('incomingCall', ...)

```typescript
addListener(eventName: 'incomingCall', listenerFunc: () => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                        |
| ------------------ | --------------------------- |
| **`eventName`**    | <code>'incomingCall'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>  |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Removes all listeners

--------------------


### Interfaces


#### TwilioVoiceOptions

| Prop              | Type                |
| ----------------- | ------------------- |
| **`accessToken`** | <code>string</code> |


#### SendDigitsOptions

| Prop       | Type                |
| ---------- | ------------------- |
| **`code`** | <code>string</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
