package com.example.broto.registerlogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity{

    public void error_handler(String s,EditText et){
        AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
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

    SharedPreferences.OnSharedPreferenceChangeListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent=getIntent();
        final String friend=intent.getStringExtra("friend");
        final String sender=intent.getStringExtra("sender");
        final String name=intent.getStringExtra("name");
        final String status=intent.getStringExtra("status");
        TextView statusfield=(TextView)findViewById(R.id.status);

        if(status.isEmpty() || status.length()<10){
            statusfield.setText("Unavailable");
        }
        else{
            statusfield.setText("Available");
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.TEMPORARY_SENDER),friend);
        editor.commit();

        TextView friendfield=(TextView)findViewById(R.id.friendfirld);
        final EditText messagefield=(EditText)findViewById(R.id.messagefield);
        Button send=(Button)findViewById(R.id.sendmessage);
        Button back=(Button)findViewById(R.id.back);
        friendfield.setText(friend);

        SharedPreferences pref=this.getSharedPreferences(getString(R.string.FCM_PREF),MODE_PRIVATE);
        listener=new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                String message=sharedPreferences.getString(getString(R.string.FCM_MESSAGE),"");
                TextView Message=(TextView)findViewById(R.id.mesgrcvd);
                Message.setText(message);
            }
        };
        pref.registerOnSharedPreferenceChangeListener(listener);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,UserAreaActivity.class);
                intent.putExtra("username",sender);
                intent.putExtra("name",name);
                startActivity(intent);
                Toast.makeText(ChatActivity.this, "Exiting Chat Console", Toast.LENGTH_SHORT).show();
            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=messagefield.getText().toString();

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
                                Toast.makeText(ChatActivity.this, "Messsage Sent! :)", Toast.LENGTH_SHORT).show();
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
                RequestQueue queue= Volley.newRequestQueue(ChatActivity.this);
                queue.add(sendTheMessage);
            }
        });
    }
}
