package com.example.broto.registerlogin;

/**
 * Created by Broto on 3/23/2017.
 */

public class ChatMessage {
    private String content;
    private boolean isMine;

    public ChatMessage(String content,boolean isMine) {
        this.content = content;
        this.isMine = isMine;
    }
    public ChatMessage(){
        this.content=null;
        this.isMine=true;
    }

    public String getContent(){
        return content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setContent(String content,boolean isMine){
        this.content=content;
        this.isMine=isMine;
    }
}
