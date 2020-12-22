import 'dart:async';

import 'package:flutter/services.dart';

class FlutterGoogleMessageApi {
  final MethodChannel _channel =
      const MethodChannel('flutter_google_message_api');
  FlutterGoogleMessageApi() {
    _channel.setMethodCallHandler(handler);
  }
  Function onSuspended;
  Function onMessage;
  Function onConnected;
  Function onInit;
  Future<void> init() async {
    try {
      return _channel.invokeMethod('init');
    } on PlatformException catch (e) {
      throw 'Unable to init';
    }
  }

  //callback function
  Future<dynamic> handler(MethodCall call) async {
    final String arg = call.arguments;
    switch (call.method) {
      case "onInit":
        if (onInit != null) onInit();
        break;
      case "onSuspended":
        if (onSuspended != null) onSuspended();
        break;
      case "onConnected":
        if (onConnected != null) onConnected();
        break;
      case "onMessageReceived":
        if (onMessage != null) onMessage(arg);
        break;
    }
  }

  Future<void> sendMessage(String message) async {
    try {
      return _channel.invokeMethod('sendMessage', <String, dynamic>{
        'message': message,
      });
    } on PlatformException catch (e) {
      throw 'Unable to send the message';
    }
  }
}
