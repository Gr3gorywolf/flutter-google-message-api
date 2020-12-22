package com.flutter.google.message.api.flutter_google_message_api;

import java.lang.Exception;
import androidx.annotation.NonNull;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.widget.Toast;
import android.content.Context;
import android.os.Bundle;

/** FlutterGoogleMessageApiPlugin */
public class FlutterGoogleMessageApiPlugin
    implements FlutterPlugin, MethodCallHandler, MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {
  /// The MethodChannel that will the communication between Flutter and native
  /// Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine
  /// and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private GoogleApiClient mApiClient;
  private Context context;
  private static final String WEAR_MESSAGE_PATH = "/message";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_google_message_api");
    channel.setMethodCallHandler(this);
    this.context = flutterPluginBinding.getApplicationContext();
  }

  private void initGoogleApiClient() {
    mApiClient = new GoogleApiClient.Builder(this.context).addApi(Wearable.API).addConnectionCallbacks(this).build();

    showToast("initializing");
    if (mApiClient != null && !(mApiClient.isConnected() || mApiClient.isConnecting()))
      mApiClient.connect();
  }

  private void showToast(String msg) {
    CharSequence text = msg;
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(this.context, text, duration);
    toast.show();
  }

  private void sendMessage(final String path, final String message) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
        for (Node node : nodes.getNodes()) {
          MessageApi.SendMessageResult result = Wearable.MessageApi
              .sendMessage(mApiClient, node.getId(), path, message.getBytes()).await();
        }
      }
    }).start();
  }

  @Override
  public void onMessageReceived(final MessageEvent messageEvent) {
    if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
      showToast(new String(messageEvent.getData()));
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    showToast("connected");
    Wearable.MessageApi.addListener(mApiClient, this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("init")) {
      try {
        initGoogleApiClient();
      } catch (Exception ex) {
        result.notImplemented();

      }

    } else if (call.method.equals("sendMessage")) {
      final String msg = call.argument("message");
      sendMessage(WEAR_MESSAGE_PATH, msg);
      result.success(null);
      showToast("sent message");
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (mApiClient != null)
      mApiClient.unregisterConnectionCallbacks(this);
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onConnectionSuspended(int i) {
    showToast("suspended");
  }
}
