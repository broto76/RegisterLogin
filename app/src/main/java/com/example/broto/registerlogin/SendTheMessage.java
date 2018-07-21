package com.example.broto.registerlogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Broto on 2/28/2017.
 */

public class SendTheMessage extends StringRequest {
    public static final String url="http://eurus.96.lt/SendMessage.php";
    private Map<String,String> params;

    public SendTheMessage(String username,String sender,String message,Response.Listener<String> listener){
        super(Method.POST,url,listener,null);
        params=new HashMap<>();
        params.put("username",username);
        params.put("sender",sender);
        params.put("message",message);
    }

    public Map<String,String> getParams(){
        return params;
    }
}
