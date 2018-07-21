package com.example.broto.registerlogin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Broto on 2/28/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG,"From: "+remoteMessage.getFrom());

        if(remoteMessage.getData().size()>0){
            Log.d(TAG, "Message Data: "+remoteMessage.getData());
        }

        if(remoteMessage.getData()!=null){
            Log.d(TAG,"Message Body: "+remoteMessage.getNotification().getBody());

        }


        sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        updatepreferences(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        updatetable(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        changedPreference(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String message,String sender){
        Intent i = new Intent(this,ChatActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Message Received!")
                .setContentText("From:"+sender)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }

    private void updatepreferences(String message,String Sender){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        //String tsender=sharedPreferences.getString(getString(R.string.TEMPORARY_SENDER),"");
        //if(tsender.contentEquals(new StringBuffer(Sender))) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.FCM_MESSAGE), message);
            editor.commit();
       // }
    }

    private void changedPreference(String sender,String message){
        SharedPreferences sharedPreferences;
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        String tsender=sharedPreferences.getString(getString(R.string.TEMPORARY_SENDER),"");
        if(sender.contentEquals(new StringBuffer(tsender))){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.RECEIVE_STATUS),message);
            editor.commit();
        }
    }

    public void updatetable(String message,String Sender){
        ChatMessageDatabaseHandler db=new ChatMessageDatabaseHandler(MyFirebaseMessagingService.this,Sender,Sender,null,1);
        db.addMessage(message,false);
        db.close();
    }
}
