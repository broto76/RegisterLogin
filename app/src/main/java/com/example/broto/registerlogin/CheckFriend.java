package com.example.broto.registerlogin;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Broto on 2/27/2017.
 */

public class CheckFriend extends StringRequest {
    private static final String Url="http://eurus.96.lt/checkfriend.php";
    private Map<String,String> params;

    public CheckFriend(String username, Response.Listener<String> listener)
    {
        super(Method.POST,Url,listener,null);
        params=new HashMap<>();
        params.put("username",username);
    }

    public Map<String,String> getParams(){
        return params;
    }
}
