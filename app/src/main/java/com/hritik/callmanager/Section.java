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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Section extends Activity {
    // List view
    ListView lv;
    String cat;
    FloatingActionButton myFab,helpFab;
    Context context=this;
    // Search EditText
    ImageButton bin;
    EditText inputSearch;
    ArrayList<String> num,nameAr;
    ArrayAdapter<String> adapter;
    String t_name,t_number;

    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    protected  void permit(Intent i){
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
        db.execSQL("DELETE FROM callmg WHERE phone='"+num1+"' AND category='"+cat+"'");
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
    public void updateContact(int pos,String newname,String newphone){
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getWritableDatabase();
        //Error cause database delete
        String ph=num.get(pos);
        db.execSQL("UPDATE callmg SET name='"+newname+"' "+"WHERE phone='"+ph+"'");
        db.execSQL("UPDATE callmg SET phone='"+newphone+"' "+"WHERE phone='"+ph+"'");
        System.out.println("Edit Contact Name");
        db.close();
        dpHelper.close();
    }
    public boolean contain(int pos,String st,ArrayList ar)
    {
        String ar_nm=st.replace(" ","");
        for(int i=0;i<ar.size();i++)
        {
            if(i==pos){continue;}
            String ar_st=ar.get(i).toString().replace(" ","");
            if(ar_nm.equalsIgnoreCase(ar_st)){
                return true;
            }
        }
        return false;
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
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.dialog_contact_new_name, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);
                    final int start_pos=pos;
                    String nm=adapter.getItem(pos);
                    int newpos=nameAr.indexOf(nm);
                    final EditText userName = (EditText) promptsView
                            .findViewById(R.id.editContactNewName);
                    userName.setText(nameAr.get(newpos));
                    final EditText userPhone = (EditText) promptsView
                            .findViewById(R.id.editContactPhone);
                    userPhone.setText(num.get(newpos));
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input and set it to result
                                            // edit text
                                            char ph_first=userPhone.getText().toString().charAt(0);
                                            if (contain(start_pos,userName.getText().toString(), nameAr) || contain(start_pos,userPhone.getText().toString(), num)) {
                                                Toast.makeText(getApplicationContext(), "Name or Number already existing in " + cat.toUpperCase(), Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                            else if (!isNum(userPhone.getText().toString().trim()))
                                            {
                                                Toast.makeText(getApplicationContext(), "Number must contain 10 digits.", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                            else if (userName.getText().toString().trim().length()< 1)
                                            {
                                                Toast.makeText(getApplicationContext(), "Name is required.", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                            else if(isEmergencyNumber(userPhone.getText().toString())){
                                                updateContact(start_pos,userName.getText().toString(),userPhone.getText().toString());
                                                Toast.makeText(getApplicationContext(),"Edit Successfully",Toast.LENGTH_SHORT).show();
                                                update_listview();
                                            }
                                            else if(ph_first=='*'){
                                                updateContact(start_pos,userName.getText().toString(),userPhone.getText().toString());
                                                Toast.makeText(getApplicationContext(),"Edit Successfully",Toast.LENGTH_SHORT).show();
                                                update_listview();
                                            }
                                            else if (isNum(userPhone.getText().toString().trim())) {
                                                updateContact(start_pos,userName.getText().toString(),userPhone.getText().toString());
                                                Toast.makeText(getApplicationContext(),"Edit Successfully",Toast.LENGTH_SHORT).show();
                                                update_listview();
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    return true;
                }
            });
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    String nm=adapter.getItem(position);
                    Intent i = new Intent(Intent.ACTION_CALL);
                    int newpos=nameAr.indexOf(nm);
                    i.setData(Uri.parse("tel:"+num.get(newpos)));
                    permit(i);
                    //Toast.makeText(context, nm+"  "+num.get(newpos)+" "+nameAr.get(newpos)+" selected!", Toast.LENGTH_LONG).show();
                }});

            myFab = (FloatingActionButton) findViewById(R.id.fab);
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
                    builder.setMessage("Are you sure you want to delete group "+cat+"?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
            helpFab = (FloatingActionButton) findViewById(R.id.section_help);
            helpFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    HelpDialog d = new HelpDialog();
                    final int bk=R.color.white;
                    final int fg=R.color.black2;
                    final int tfg=R.color.white;
                    final int tbk=R.color.tblue;
                    String title="Group "+cat.toUpperCase()+" Help";
                    String desc="1)To delete group click on the bin symbol button.\n\n" +
                            "2)To edit contact,press on the contact for few seconds.\n\n"+
                            "3)To call contact,click on the respective contact.\n\n"+
                            "4)To delete contact,swipe the contact irrespective of direction(left or right).";
                    d.onStartDialog(context,title,desc,bk,fg,tbk,tfg);
                }
            });
        }
        catch (Exception e){e.printStackTrace();}

    }
    private boolean isEmergencyNumber(String num) {
        ArrayList<String> emergency_no= new ArrayList<String>(
                Arrays.asList("112","100","101", "102","1091","108","139","1091","1070"));
        //ArrayList<String> emergency_name= new ArrayList<String>(
        //    Arrays.asList("NATIONAL EMERGENCY NUMBER","POLICE","FIRE", "AMBULANCE","Women Helpline","Disaster Management Services","Railway Enquiry","Senior Citizen Helpline","Natural Calamities Helpline"));
        for(int i=0;i<emergency_no.size();i++)
        {
            if (num.equals(emergency_no.get(i)))
            {
                return true;
            }
        }
        return false;
    }
    private boolean isNum(String ph){
        Pattern pattern = Pattern.compile("\\d{10}$");
        Matcher matcher = pattern.matcher(ph);
        return matcher.matches();
    }
}
