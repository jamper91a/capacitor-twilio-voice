#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(TwilioVoicePlugin, "TwilioVoice",
           CAP_PLUGIN_METHOD(registerDevice, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(acceptCall, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(disconnect, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(sendDigits, CAPPluginReturnPromise);
)
