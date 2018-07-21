package com.example.broto.registerlogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatUI extends AppCompatActivity {

    public void error_handler(String s,EditText et){
        AlertDialog.Builder builder=new AlertDialog.Builder(ChatUI.this);
        builder.setMessage(s)
                .setNegativeButton("Retry",null)
                .create()
                .show();
        et.setText(null);
    }

    public boolean checkInternet(){
        ConnectionDetector cd=new ConnectionDetector(this);
        return cd.isConnected();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener listener;


    private ListView listView;
    private Button btnSend;
    private Button btnClr;
    private EditText messagefield;
    private TextView Friend;
    private TextView Status;
    private List<ChatMessage> chatMessages;
    private ArrayAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ui);

        Friend = (TextView) findViewById(R.id.Friend);
        Status = (TextView) findViewById(R.id.Status);

        chatMessages = new ArrayList<>();
        Intent intent=getIntent();
        final String friend=intent.getStringExtra("friend");
        final String sender=intent.getStringExtra("sender");
        final String name=intent.getStringExtra("name");
        final String status=intent.getStringExtra("status");

        Friend.setText(friend);

        if(status.isEmpty() || status.length()<10){
            Status.setText("Unavailable");
        }
        else{
            Status.setText("Available");
        }

        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnClr = (Button) findViewById(R.id.btn_clr);
        messagefield = (EditText) findViewById(R.id.msg_type);


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.TEMPORARY_SENDER),friend);
        editor.commit();

        //ChatMessageDatabaseHandler db=new ChatMessageDatabaseHandler(this,friend,friend,null,1);
        ChatMessage chatMessage[]/*=db.retrieveMessages()*/;
        chatMessage=null;
        //db.close();

        if(chatMessage==null){
            ChatMessage First=new ChatMessage("Hi. Ping Me!",false);
            chatMessages.add(First);
        }
        else{
            for(int i=0;i<chatMessage.length;++i){
                chatMessages.add(chatMessage[i]);
            }
        }

        adapter = new MessageAdapter(this,R.layout.item_chat_left,chatMessages);
        listView.setAdapter(adapter);

        SharedPreferences pref=this.getSharedPreferences(getString(R.string.FCM_PREF),MODE_PRIVATE);
        listener= new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                /*sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                String message=sharedPreferences.getString(getString(R.string.FCM_MESSAGE),"");
                ChatMessage chatMessage = new ChatMessage(message,false);
                chatMessages.add(chatMessage);
                adapter.notifyDataSetChanged();*/

                notifyUI();
            }
        };
        pref.registerOnSharedPreferenceChangeListener(listener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String message=messagefield.getText().toString();

                if(message.isEmpty()){
                    error_handler("Dont Be Lazy Now!",messagefield);
                    return;
                }

                if(!checkInternet()){
                    error_handler("No Internet Connection",messagefield);
                    return;
                }

                Response.Listener<String> reponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success=jsonObject.getString("success");
                            if(success.equals("true")){
                                notifyUIChange(friend,message);
                                Toast.makeText(ChatUI.this, "Messsage Sent! :)", Toast.LENGTH_SHORT).show();
                                messagefield.setText(null);
                            }
                            else if(success.equals("false")){
                                //error_handler("No Friend Found!",friendfield);
                                return;
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            error_handler("No JASON Object Returned!!", messagefield);
                        }
                    }
                };

                SendTheMessage sendTheMessage = new SendTheMessage(friend, sender, message, reponseListener);
                RequestQueue queue= Volley.newRequestQueue(ChatUI.this);
                queue.add(sendTheMessage);
            }
        });

        btnClr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
            }
        });
    }

    private void clearUI(){
        adapter.clear();
        ChatMessage First=new ChatMessage("Hi. Ping Me!",false);
        adapter.add(First);
        //adapter.notifyDataSetChanged();
        adapter = new MessageAdapter(this,R.layout.item_chat_left,chatMessages);
        listView.setAdapter(adapter);
         /* ChatMessageDatabaseHandler db = new ChatMessageDatabaseHandler(ChatUI.this,friend,friend,null,1);
                db.clearTable();
                db.close();*/
    }

    private void notifyUIChange(String friend,String message){
        //ChatMessageDatabaseHandler db = new ChatMessageDatabaseHandler(ChatUI.this,friend,friend,null,1);
        //db.addMessage(message,false);
        ChatMessage chatMessage = new ChatMessage(message,true);
        chatMessages.add(chatMessage);
        //adapter.notifyDataSetChanged();
        adapter = new MessageAdapter(this,R.layout.item_chat_left,chatMessages);
        listView.setAdapter(adapter);
        //db.close();
    }

    private void notifyUI(){
        SharedPreferences sharedPreferences;
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        String message=sharedPreferences.getString(getString(R.string.FCM_MESSAGE),"");
        //String message  =sharedPreferences.getString(getString(R.string.FCM_MESSAGE),"");
        //ChatMessageDatabaseHandler db = new ChatMessageDatabaseHandler(ChatUI.this,friend,friend,null,1);
        ChatMessage chatMessage = new ChatMessage(message,false);
        chatMessages.add(chatMessage);
        //adapter.notifyDataSetChanged();
        adapter = new MessageAdapter(this,R.layout.item_chat_left,chatMessages);
        listView.setAdapter(adapter);
        //db.close();
    }
}
