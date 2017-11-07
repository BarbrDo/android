package com.barbrdo.app.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AcceptRejectRequestActivity;
import com.barbrdo.app.activities.AlertDialogActivity;
import com.barbrdo.app.activities.AwaitingBarberApprovalActivity;
import com.barbrdo.app.activities.MessageDialogActivity;
import com.barbrdo.app.activities.RateBarberActivity;
import com.barbrdo.app.activities.SplashActivity;
import com.barbrdo.app.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        sendNotification(remoteMessage);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts 
    private void sendNotification(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().get("my_key").toString().equalsIgnoreCase("customer_request_to_barber")) {
            Intent intent = new Intent(this, AcceptRejectRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.BundleKeyTag.SERIALIZED_DATA, remoteMessage.getData().get("message").toString());
            startActivity(intent);
        } else if (remoteMessage.getData().get("my_key").toString().equalsIgnoreCase("customer_cancel_appointment")) {
            Intent intentMessage = new Intent(AcceptRejectRequestActivity.RECEIVE_CUSTOMER_MESSAGES);
            intentMessage.putExtra(Constants.BundleKeyTag.MESSAGE, remoteMessage.getData().get("my_key").toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentMessage);

            Intent i = new Intent(this, AlertDialogActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else if (remoteMessage.getData().get("my_key").toString().equalsIgnoreCase("barber_cancel_appointment")) {
            Intent intentMessage = new Intent(AwaitingBarberApprovalActivity.RECEIVE_BARBER_MESSAGES);
            intentMessage.putExtra(Constants.BundleKeyTag.MESSAGE, remoteMessage.getData().get("my_key").toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentMessage);
        } else if (remoteMessage.getData().get("my_key").toString().equalsIgnoreCase("barber_confirm_appointment")) {
            Intent intentMessage = new Intent(AwaitingBarberApprovalActivity.RECEIVE_BARBER_MESSAGES);
            intentMessage.putExtra(Constants.BundleKeyTag.MESSAGE, remoteMessage.getData().get("my_key").toString());
            intentMessage.putExtra(Constants.BundleKeyTag.SERIALIZED_DATA, remoteMessage.getData().get("message").toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentMessage);
        } else if (remoteMessage.getData().get("my_key").toString().equalsIgnoreCase("barber_complete_appointment")) {
            Intent intent = new Intent(this, RateBarberActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.BundleKeyTag.IS_CHECK_IN, true);
            startActivity(intent);
        } else if (remoteMessage.getData().get("my_key").toString().equalsIgnoreCase("message_to_barber")) {
            Intent intent = new Intent(this, MessageDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.BundleKeyTag.SERIALIZED_DATA, remoteMessage.getData().get("message").toString());
            intent.putExtra(Constants.BundleKeyTag.MESSAGE_TO_BARBER, true);
            startActivity(intent);
        } else if (remoteMessage.getData().get("my_key").toString().equalsIgnoreCase("message_to_customer")) {
            Intent intent = new Intent(this, MessageDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.BundleKeyTag.SERIALIZED_DATA, remoteMessage.getData().get("message").toString());
            intent.putExtra(Constants.BundleKeyTag.MESSAGE_TO_BARBER, false);
            startActivity(intent);
        }

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound_accept_decline);

        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("BarbrDo")
                    .setContentText(Html.fromHtml(remoteMessage.getData().get("my_another_key").toString()))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_192)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle("BarbrDo")
                    .setContentText(Html.fromHtml(remoteMessage.getData().get("my_another_key").toString()))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), notificationBuilder.build());
    }
}