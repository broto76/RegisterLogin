package com.example.broto.registerlogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by broto on 1/19/17.
 */

public class RegisterRequest extends StringRequest {
    private static final String RegisterRequestUrl="http://eurus.96.lt/Register2.php";
    private Map<String,String> params;

    public RegisterRequest(String name, String username, String password, String email, Response.Listener<String> listener)
    {

        super(Method.POST,RegisterRequestUrl,listener,null);
        params=new HashMap<>();
        params.put("name",name);
        params.put("username",username);
        params.put("password",password);
        params.put("email",email);
    }

    public Map<String,String> getParams(){
        return params;
    }
}
