
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterGoogleMessageApi {
  static const MethodChannel _channel =
      const MethodChannel('flutter_google_message_api');
    static Future<void> init() async {
    // Errors occurring on the platform side cause invokeMethod to throw
    // PlatformExceptions.
    try {
      return _channel.invokeMethod('init');
    } on PlatformException catch (e) {
      throw 'Unable to init';
    }
  }
  static Future<void> sendMessage(String message) async {
    // Errors occurring on the platform side cause invokeMethod to throw
    // PlatformExceptions.
    try {
      return _channel.invokeMethod('sendMessage', <String, dynamic>{
        'message': message,
      });
    } on PlatformException catch (e) {
      throw 'Unable to send the message';
    }
  }
   


}
