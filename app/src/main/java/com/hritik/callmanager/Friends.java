package com.hritik.callmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class Friends extends AppCompatActivity {

    // List view
    ListView lv;

    // Search EditText
    EditText inputSearch;
    ArrayList<String> num,friends;

    ArrayAdapter<String> adapter;
    protected  void permit(Intent i,int position){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CALL_PHONE}, 100);
            return;
        }
        startActivity(i);
    }
    public void delete(String num1) {
        try {
            MyHelper dpHelper = new MyHelper(this);
            SQLiteDatabase db = dpHelper.getWritableDatabase();
            db.delete("callmg", "phone=" + num1, null);
            System.out.println("Delete");
            db.close();
            dpHelper.close();
        }
        catch (Exception e){}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        friends = new ArrayList<String>();
        num= new ArrayList<String>();

        try {
            Cursor cursor =dpHelper.alldata("Friends");
            while(cursor.moveToNext()) {
                friends.add(cursor.getString(cursor.getColumnIndex("name")));
                num.add(cursor.getString(cursor.getColumnIndex("phone")));
            }
        }
        catch (Exception e){e.printStackTrace();}

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.search);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, friends);
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
                                    friends.remove(position);
                                    adapter.notifyDataSetChanged();

                                }


                            }
                        });
        lv.setOnTouchListener(touchListener);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Friends.this.adapter.getFilter().filter(cs);
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
                i.putExtra("cat", "Friends");
                startActivity(i);
            }
        });
    }
}
