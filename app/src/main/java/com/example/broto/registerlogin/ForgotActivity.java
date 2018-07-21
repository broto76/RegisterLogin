package com.example.broto.registerlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotActivity extends AppCompatActivity {

    EditText email;
    Button reset;
    private ProgressDialog progressDialog;

    public void forgot_error_handler(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotActivity.this);
        builder.setMessage(s)
                .setNegativeButton("OKAY", null)
                .create()
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        email=(EditText)findViewById(R.id.email);
        reset=(Button)findViewById(R.id.reset);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email=email.getText().toString();
                if(Email.isEmpty()){
                    forgot_error_handler("Please enter your email");
                    return;
                }

                progressDialog=new ProgressDialog(ForgotActivity.this);
                progressDialog.setMessage("Verifying Email. Please Wait!");
                progressDialog.show();

                Response.Listener<String> ResponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            progressDialog.dismiss();
                            boolean success = jsonResponse.getBoolean("success");
                            if(!success){
                                forgot_error_handler("Email does not exist");
                                email.setText(null);
                            }
                            else{
                                forgot_error_handler("Please check your mail for the password");
                            }

                        } catch (JSONException e) {
                            forgot_error_handler("No Jason Object Returned");
                            e.printStackTrace();
                        }
                    }
                };

                ForgotPasswordRequest ForgotRequest = new ForgotPasswordRequest(Email, ResponseListener);
                RequestQueue queue = Volley.newRequestQueue(ForgotActivity.this);
                queue.add(ForgotRequest);

            }
        });
    }
    public void onBackPressed(){
        Intent intent=new Intent(ForgotActivity.this,LoginActivity.class);
        startActivity(intent);
        ForgotActivity.this.finish();

    }
}
