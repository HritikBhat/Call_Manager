package com.hritik.callmanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

public class Section extends Activity {
    // List view
    ListView lv;
    String cat;
    Context context=this;
    // Search EditText
    ImageButton bin;
    EditText inputSearch;
    ArrayList<String> num,nameAr;
    ArrayAdapter<String> adapter;
    String t_name,t_number;

    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    protected  void permit(Intent i, int position){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CALL_PHONE}, 100);
            return;
        }
        startActivity(i);
    }
    public void main_Intent(){
        Intent i2 = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i2);
    }
    public void delete_grp(){
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getWritableDatabase();
        //Error cause database delete
        db.execSQL("DELETE FROM callmg WHERE category='"+cat+"'");
        db.execSQL("DELETE FROM catnm WHERE cname='"+cat+"'");
        System.out.println("Delete GRP");
        db.close();
        dpHelper.close();
        main_Intent();
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
    private void insert(String name,String phone){
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("name", name);
        insertValues.put("category", cat);
        insertValues.put("phone", phone);
        long rows =db.insert("callmg", null, insertValues);
        System.out.println(rows);
        //Permission is being asked
    }
    public void update_listview(){
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        bin=findViewById(R.id.bin);
        nameAr = new ArrayList<String>();
        num= new ArrayList<String>();
        Intent inr=getIntent();
        cat = inr.getExtras().getString("cat");

        try {
            Cursor cursor =dpHelper.alldata(cat);
            while(cursor.moveToNext()) {
                System.out.println(cursor.getString(cursor.getColumnIndex("name")));
                nameAr.add(cursor.getString(cursor.getColumnIndex("name")));
                num.add(cursor.getString(cursor.getColumnIndex("phone")));
            }
        }
        catch (Exception e){e.printStackTrace();}

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.search);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, nameAr);
        lv.setAdapter(adapter);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        try {
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.cord1);
            update_listview();
            //Swipe
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
                                        System.out.print("Deleted!");
                                        t_number=num.get(position);
                                        t_name=nameAr.get(position);
                                        delete(num.get(position));
                                        nameAr.remove(position);

                                        Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, "Contact has been deleted.", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Contact is restored!", Snackbar.LENGTH_SHORT);
                                                        insert(t_name,t_number);
                                                        update_listview();
                                                        snackbar1.show();
                                                    }
                                                }).setActionTextColor(Color.WHITE);


                                        snackbar.show();
                                        adapter.notifyDataSetChanged();

                                    }


                                }
                            });
            lv.setOnTouchListener(touchListener);

            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    Section.this.adapter.getFilter().filter(cs);
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
                    //Toast.makeText(context, num.get(position)+" "+nameAr.get(position)+" selected!", Toast.LENGTH_SHORT).show();
                }});

            FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(),Insertion.class);
                    i.putExtra("cat", cat);
                    startActivity(i);
                }
            });
            bin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Dialog
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    delete_grp();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete "+cat+"?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
        }
        catch (Exception e){e.printStackTrace();}

    }}
