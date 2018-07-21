package com.example.broto.registerlogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Broto on 2/2/2017.
 */

public class ForgotPasswordRequest extends StringRequest{
    private static final String RegisterRequestUrl="http://eurus.96.lt/ForgotPassword.php";
    private Map<String,String> params;

    public ForgotPasswordRequest(String email, Response.Listener<String> listener)
    {

        super(Method.POST,RegisterRequestUrl,listener,null);
        params=new HashMap<>();
        params.put("email",email);
    }

    public Map<String,String> getParams(){
        return params;
    }
}
