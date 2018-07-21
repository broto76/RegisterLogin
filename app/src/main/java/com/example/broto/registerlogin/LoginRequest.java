package com.example.broto.registerlogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by broto on 1/22/17.
 */

public class LoginRequest extends StringRequest {
    private static final String RegisterRequestUrl="http://eurus.96.lt/Login2.php";
    private Map<String,String> params;

    public LoginRequest(String username, String password, String fcm_key,Response.Listener<String> listener)
    {

        super(Method.POST,RegisterRequestUrl,listener,null);
        params=new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        params.put("fcm_key",fcm_key);
    }

    public Map<String,String> getParams(){
        return params;
    }
}
