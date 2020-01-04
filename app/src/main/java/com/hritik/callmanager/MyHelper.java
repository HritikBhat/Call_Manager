package com.hritik.callmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;

public class MyHelper extends SQLiteOpenHelper
{
    public MyHelper(Context context){
        super(context,"callmg.db",null,1);
    }
    public void onCreate(SQLiteDatabase db){
        try {
            db.execSQL("create table callmg(cid int PRIMARY KEY,name text,category text,phone text)");
            //db.execSQL("CREATE UNIQUE INDEX idx_category ON callmg(category);");
        } catch (SQLiteException e) {
            try {
                throw new IOException(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
    }}
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {
        db.execSQL("drop table if exists callmg");
        onCreate(db);
    }
    public Cursor alldata(String cat){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM callmg WHERE TRIM(category) = '" + cat.trim() + "'";
        Cursor  cursor = db.rawQuery(query,null);
        return cursor;
    }
    public int getRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM callmg";
        Cursor  cursor = db.rawQuery(query,null);
        return cursor.getCount();
    }
    public void onDelete(String phone_num){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM callmg WHERE TRIM(phone) = '" + phone_num.trim() + "'";
        Cursor  cursor = db.rawQuery(query,null);
    }
}

