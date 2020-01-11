package com.hritik.callmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class selectContact extends AppCompatActivity {

    private ListView lv;
    private String cat;
    private ArrayList<Model> modelArrayList;
    private CustomAdapter customAdapter;
    private Button btnselect, btndeselect, done;
    private  ArrayList<String> ar_name = new ArrayList();
    private  ArrayList<String> ar_phone = new ArrayList();

    private void per_readContact(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, 100);

        }
        else{}
    }
    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        ar_name.add(name);
                        ar_phone.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }

    private void register()
    {
        MyHelper dpHelper = new MyHelper(getApplicationContext());
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        for (int i = 0; i < CustomAdapter.modelArrayList.size(); i++){
            if(CustomAdapter.modelArrayList.get(i).getSelected()) {
                ContentValues insertValues = new ContentValues();
                insertValues.put("name", CustomAdapter.modelArrayList.get(i).getName());
                insertValues.put("category", cat);
                insertValues.put("phone",ar_phone.get(i).replace("+91","").trim());
                long rows =db.insert("callmg", null, insertValues);
            }
        }
        dpHelper.close();
        db.close();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        Intent i=getIntent();
        //Asks permission to read contact
        per_readContact();
        //Retrieve all contact list
        getContactList();
        cat = i.getExtras().getString("cat");
        lv = (ListView) findViewById(R.id.lv);
        btnselect = (Button) findViewById(R.id.select);
        btndeselect = (Button) findViewById(R.id.deselect);
        done = (Button) findViewById(R.id.next);

        modelArrayList = getModel(false);
        customAdapter = new CustomAdapter(this,modelArrayList);
        lv.setAdapter(customAdapter);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelArrayList = getModel(true);
                customAdapter = new CustomAdapter(getApplicationContext(),modelArrayList);
                lv.setAdapter(customAdapter);
            }
        });
        btndeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelArrayList = getModel(false);
                customAdapter = new CustomAdapter(getApplicationContext(),modelArrayList);
                lv.setAdapter(customAdapter);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    private ArrayList<Model> getModel(boolean isSelect){
        ArrayList<Model> list = new ArrayList<>();
        for(int i = 0; i < ar_name.size(); i++){
            Model model = new Model();
            model.setSelected(isSelect);
            model.setName(ar_name.get(i));
            list.add(model);
        }
        return list;
    }
}

