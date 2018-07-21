package com.example.broto.registerlogin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by Broto on 3/24/2017.
 */

public class ChatMessageDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME=null;
    private static String TABLE_NAME=null;

    private static final String COLUMN_MESSAGE="message";
    private static final String COLUMN_ISMINE="ismine";

    public ChatMessageDatabaseHandler(Context context, String dbName, String tableName, SQLiteDatabase.CursorFactory factory,int version){
        super(context,dbName,factory,version);
        DATABASE_NAME=dbName;
        TABLE_NAME=tableName;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+"("+COLUMN_MESSAGE+" TEXT,"+COLUMN_ISMINE+" TEXT);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addMessage(String Message,Boolean IsMine){
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE,Message);
        values.put(COLUMN_ISMINE,IsMine.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ChatMessage[] retrieveMessages(){
        String query="SELECT * FROM "+TABLE_NAME+";";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount()<1){
            return null;                    // No Messages available yet
        }

        cursor.moveToFirst();
        int counter=-1;

        ChatMessage chatMessage[]=new ChatMessage[cursor.getCount()];

        do{
            counter++;
            chatMessage[counter].setContent(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)),
                    Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ISMINE))));

        }while(cursor.moveToNext());

        cursor.close();
        db.close();

        return chatMessage;
    }

    public ChatMessage getLast(){
        String query="SELECT * FROM "+TABLE_NAME+";";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        cursor.moveToLast();

        ChatMessage chatMessage = new ChatMessage(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)),
                Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ISMINE))));

        cursor.close();
        db.close();

        return chatMessage;
    }

    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        /*db.execSQL("TRUNCATE TABLE "+TABLE_NAME+";");
        db.close();*/
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        db.close();
    }
}
