package com.example.broto.registerlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class UserAreaActivity extends AppCompatActivity {

    public void error_handler(String s,EditText et){
        AlertDialog.Builder builder=new AlertDialog.Builder(UserAreaActivity.this);
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

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        TextView welcomemessage=(TextView)findViewById(R.id.welcomemessage);
        Button logout=(Button)findViewById(R.id.logout);
        Button chat=(Button)findViewById(R.id.chat);
        Button uichat=(Button)findViewById(R.id.uichat);
        Intent intent=getIntent();
        String welcome="Welcome ";
        final String Uname=intent.getStringExtra("username");
        final String Name=intent.getStringExtra("name");
        welcome=welcome.concat(intent.getStringExtra("name"));
        welcomemessage.setText(welcome);

        uichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText friendfield=(EditText)findViewById(R.id.friend);
                final String friend=friendfield.getText().toString();

                if(friend.isEmpty()){
                    error_handler("No Username",friendfield);
                    return;
                }

                if(!checkInternet()){
                    error_handler("No Internet Connection",friendfield);
                    return;
                }

                progressDialog=new ProgressDialog(UserAreaActivity.this);
                progressDialog.setMessage("Finding Friend. Please Wait. ");
                progressDialog.show();

                Response.Listener<String> reponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            progressDialog.dismiss();
                            String success=jsonObject.getString("success");
                            if(success.equals("true")){
                                Intent intent=new Intent(UserAreaActivity.this,ChatUI.class);
                                intent.putExtra("friend",friend);
                                intent.putExtra("sender",Uname);
                                intent.putExtra("name",Name);
                                intent.putExtra("status",jsonObject.getString("status"));
                                startActivity(intent);
                                Toast.makeText(UserAreaActivity.this, "Friend Found!! :)", Toast.LENGTH_SHORT).show();
                                //UserAreaActivity.this.finish();
                            }
                            else if(success.equals("false")){
                                progressDialog.dismiss();
                                error_handler("No Friend Found!",friendfield);
                                return;
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            error_handler("No JASON Object Returned!!",friendfield);
                        }
                    }
                };

                CheckFriend checkFriend = new CheckFriend(friend, reponseListener);
                RequestQueue queue= Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(checkFriend);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText friendfield=(EditText)findViewById(R.id.friend);
                final String friend=friendfield.getText().toString();

                if(friend.isEmpty()){
                    error_handler("No Username",friendfield);
                    return;
                }

                if(!checkInternet()){
                    error_handler("No Internet Connection",friendfield);
                    return;
                }

                progressDialog=new ProgressDialog(UserAreaActivity.this);
                progressDialog.setMessage("Finding Friend. Please Wait. ");
                progressDialog.show();

                Response.Listener<String> reponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            progressDialog.dismiss();
                            String success=jsonObject.getString("success");
                            if(success.equals("true")){
                                Intent intent=new Intent(UserAreaActivity.this,ChatActivity.class);
                                intent.putExtra("friend",friend);
                                intent.putExtra("sender",Uname);
                                intent.putExtra("name",Name);
                                intent.putExtra("status",jsonObject.getString("status"));
                                startActivity(intent);
                                Toast.makeText(UserAreaActivity.this, "Friend Found!! :)", Toast.LENGTH_SHORT).show();
                                //UserAreaActivity.this.finish();
                            }
                            else if(success.equals("false")){
                                progressDialog.dismiss();
                                error_handler("No Friend Found!",friendfield);
                                return;
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            error_handler("No JASON Object Returned!!",friendfield);
                        }
                    }
                };

                CheckFriend checkFriend = new CheckFriend(friend, reponseListener);
                RequestQueue queue= Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(checkFriend);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.USERNAME),"");
                editor.putString(getString(R.string.PASSWORD),"");
                editor.commit();
                Intent intent1=new Intent(UserAreaActivity.this,LoginActivity.class);
                startActivity(intent1);
                UserAreaActivity.this.finish();
            }
        });
    }
    boolean doubleBack=false;

    @Override
    public void onBackPressed() {

        if(doubleBack){
            super.onBackPressed();
            /*Intent intent=new Intent(getApplicationContext(),UserAreaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT",true);
            startActivity(intent);*/
        }
            doubleBack=true;
        Toast.makeText(this, "Press BACK again to exit!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBack=false;
            }
        },2000) ;


    }

}
