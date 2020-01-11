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
            db.execSQL("create table callmg(name text,category text,phone text)");
            db.execSQL("create table catnm(cname text)");
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
        String query = "SELECT * FROM callmg WHERE TRIM(category) = '" + cat.trim() + "' ORDER BY name ASC";
        Cursor  cursor = db.rawQuery(query,null);
        return cursor;
    }

    public Cursor getCategoryNames(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT cname FROM catnm";
        Cursor  cursor = db.rawQuery(query,null);
        return cursor;
    }

    public int getRows(){
        String countQuery = "SELECT  * FROM callmg";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public void onDelete(String phone_num){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM callmg WHERE TRIM(phone) = '" + phone_num.trim() + "'";
        Cursor  cursor = db.rawQuery(query,null);
    }
}

