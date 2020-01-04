package com.hritik.callmanager;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Family extends Activity {

    // List view
    ListView lv;

    // Search EditText
    EditText inputSearch;
    ArrayList<String> num,family;

    ArrayAdapter<String> adapter;

    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    protected  void permit(Intent i,int position){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CALL_PHONE}, 100);
            return;
        }
        startActivity(i);
    }
    public void delete(String num1) {
            MyHelper dpHelper = new MyHelper(this);
            SQLiteDatabase db = dpHelper.getWritableDatabase();
            //Error cause database delete
            db.execSQL("DELETE FROM callmg WHERE phone='"+num1+"'");
            System.out.println("Delete");
            db.close();
            dpHelper.close();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        family = new ArrayList<String>();
        num= new ArrayList<String>();

        try {
            Cursor  cursor =dpHelper.alldata("Family");
            while(cursor.moveToNext()) {
                System.out.println(cursor.getString(cursor.getColumnIndex("name")));
                family.add(cursor.getString(cursor.getColumnIndex("name")));
                num.add(cursor.getString(cursor.getColumnIndex("phone")));
            }
        }
        catch (Exception e){e.printStackTrace();}

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.search);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, family);
        lv.setAdapter(adapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    //On Dismiss
                                    delete(num.get(position));
                                    family.remove(position);

                                    adapter.notifyDataSetChanged();

                                }


                            }
                        });
        lv.setOnTouchListener(touchListener);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Family.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+num.get(position)));
                permit(i,position);

            }});

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Insertion.class);
                i.putExtra("cat", "Family");
                startActivity(i);
            }
        });
    }}