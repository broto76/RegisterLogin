package com.example.broto.registerlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    public void registration_error_handler(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setMessage(s)
                .setNegativeButton("Retry", null)
                .create()
                .show();
    }

    public boolean checkInternet() {
        ConnectionDetector cd = new ConnectionDetector(this);
        return cd.isConnected();
    }

    public boolean validateUserName(String s) {
        char c;
        int temp;
        for (int i = 0; i < s.length(); ++i) {
            c = s.charAt(i);
            temp = (int) c;
            if (temp > 47 && temp < 58) {
                continue;
            } else if (temp > 64 && temp < 91) {
                continue;
            } else if (temp > 96 && temp < 123) {
                continue;
            } else if (c == '_') {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean validatePassword(String s) {
        if (s.length() > 6 && s.equals(s))
            return true;
        else
            return false;
    }

    public boolean comparePass(String s, String r) {
        if (s.equals(r))
            return true;
        else
            return false;
    }

    public boolean validateEmail(String s) {
        if (s.indexOf('@') == -1)
            return false;
        if (s.indexOf('.', s.indexOf('@')) == -1)
            return false;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        final EditText uname, pwd, name, email, rpwd;
        CheckBox switch_pass = (CheckBox) findViewById(R.id.switchpass);
        Button signup;
        uname = (EditText) findViewById(R.id.username);
        pwd = (EditText) findViewById(R.id.pwd);
        rpwd = (EditText) findViewById(R.id.retypePass);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        signup = (Button) findViewById(R.id.signup);

        switch_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    rpwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    pwd.setInputType(129);
                    rpwd.setInputType(129);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Uname = uname.getText().toString();
                final String Pwd = pwd.getText().toString();
                final String Name = name.getText().toString();
                final String Email = email.getText().toString();
                final String Rpwd = rpwd.getText().toString();

                if (Uname.isEmpty() || Pwd.isEmpty() || Name.isEmpty() || Email.isEmpty()) {
                    registration_error_handler("One or More Fields are empty!");
                    return;
                }

                if (validateUserName(Uname) == false) {
                    registration_error_handler("Invalid Username");
                    return;
                }

                if (validateEmail(Email) == false) {
                    registration_error_handler("Invalid Email");
                    return;
                }

                if (validatePassword(Pwd) == false) {
                    registration_error_handler("The Password is too short!");
                    return;
                }
                if (!comparePass(Pwd, Rpwd)) {
                    registration_error_handler("Passwords do not match!");
                    return;
                }

                if (checkInternet() == false) {
                    registration_error_handler("No Internet Connection");
                    return;
                }

                progressDialog=new ProgressDialog(SignupActivity.this);
                progressDialog.setMessage("Registering User. Please Wait.");
                progressDialog.show();

                Response.Listener<String> ResponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            progressDialog.dismiss();
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Toast.makeText(SignupActivity.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                SignupActivity.this.startActivity(intent);
                                SignupActivity.this.finish();

                            } else {
                                registration_error_handler("Username Already Taken");
                            }
                        } catch (JSONException e) {
                            registration_error_handler("No Jason Object Returned");
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(Name, Uname, Pwd, Email, ResponseListener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(registerRequest);
            }

        });


    }



}
