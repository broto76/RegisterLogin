package com.example.broto.registerlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.broto.registerlogin.R.id.pwd;
import static com.example.broto.registerlogin.R.id.signin;
import static com.example.broto.registerlogin.R.id.signup;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    public void login_error_handler(String s,EditText et){
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(s)
                .setNegativeButton("Retry",null)
                .create()
                .show();
        if(et!=null)
        et.setText(null);
    }

    public boolean check_fields(String username,String password){
        if(username.isEmpty() || password.isEmpty()){
            return false;
        }
        return true;
    }

    public boolean checkInternet(){
        ConnectionDetector cd=new ConnectionDetector(this);
        return cd.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        final String UserName=sharedPreferences.getString(getString(R.string.USERNAME),"");
        final String PassWord=sharedPreferences.getString(getString(R.string.PASSWORD),"");


        if(!UserName.isEmpty() && !PassWord.isEmpty()){
            progressDialog=new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging In "+UserName+". Please Wait");
            progressDialog.show();

            Response.Listener<String> reponseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        progressDialog.dismiss();
                        String success=jsonObject.getString("success");
                        if(success.equals("true")){
                            String Username=jsonObject.getString("username");
                            String Name=jsonObject.getString("name");
                            Intent intent=new Intent(LoginActivity.this,UserAreaActivity.class);
                            intent.putExtra("name",Name);
                            intent.putExtra("username",UserName);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "SignIn Success", Toast.LENGTH_SHORT).show();
                        }
                        else if(success.equals("false")){
                            //login_error_handler("Invalid Username and/or Password!",pwd);
                        }
                        else{
                            //login_error_handler("Please Verify the email linked to this account!",pwd);
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                        login_error_handler("No Jason Object Returned!",null);
                    }
                }
            };



            String token=sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");


            LoginRequest loginRequest = new LoginRequest(UserName, PassWord, token, reponseListener);
            RequestQueue queue= Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        }



        Button signin, signup;
        final EditText uname, pwd;
        TextView forgotpass;
        CheckBox switch_pass=(CheckBox) findViewById(R.id.togglepass);
        final CheckBox loggedin=(CheckBox) findViewById(R.id.loggedin);
        signin = (Button) findViewById(R.id.signin);
        signup = (Button) findViewById(R.id.signup);
        uname = (EditText) findViewById(R.id.uname);
        pwd = (EditText) findViewById(R.id.pwd);
        forgotpass = (TextView) findViewById(R.id.forgot);
        //uname.setText(Uname);
        //pwd.setText(Pwd);

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        switch_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                else{
                    pwd.setInputType(129);
                }
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Uname = uname.getText().toString();
                final String Pwd = pwd.getText().toString();

                if(check_fields(Uname,Pwd)==false){
                    login_error_handler("One or More fields are empty",pwd);
                    return;
                }

                if(checkInternet()==false){
                    login_error_handler("No Internet Connection",pwd);
                    return;
                }

                progressDialog=new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Logging In. Please Wait");
                progressDialog.show();


                Response.Listener<String> reponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            progressDialog.dismiss();
                            String success=jsonObject.getString("success");
                            if(success.equals("true")){

                                //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getString(R.string.PASSWORD), Pwd);
                                editor.putString(getString(R.string.USERNAME), Uname);
                                editor.commit();

                                String Username=jsonObject.getString("username");
                                String Name=jsonObject.getString("name");
                                Intent intent=new Intent(LoginActivity.this,UserAreaActivity.class);
                                intent.putExtra("name",Name);
                                intent.putExtra("username",Uname);
                                LoginActivity.this.finish();
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "SignIn Success", Toast.LENGTH_SHORT).show();
                            }
                            else if(success.equals("false")){
                                login_error_handler("Invalid Username and/or Password!",pwd);
                            }
                            else{
                                login_error_handler("Please Verify the email linked to this account!",pwd);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                //sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                String token=sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");

                LoginRequest loginRequest = new LoginRequest(Uname, Pwd, token, reponseListener);
                RequestQueue queue= Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

    }


    //boolean doubleBack=false;
    private static long back_pressed;
    private static final int TIME_DELAY = 2000;

    @Override
    public void onBackPressed() {

        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();


    }
}

