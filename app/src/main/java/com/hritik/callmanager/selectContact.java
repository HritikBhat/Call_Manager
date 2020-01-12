package com.hritik.callmanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

public class selectContact extends AppCompatActivity {

    private ListView lv;
    private String cat;
    private ArrayList<Model> modelArrayList;
    ArrayList<String> nameAr= new ArrayList<String>();
    ArrayList<String> num= new ArrayList<String>();
    private CustomAdapter customAdapter;
    private EditText searchc;
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
    private boolean contain(String st,ArrayList ar)
    {
        String ar_nm=st.replace(" ","");
        for(int i=0;i<ar.size();i++)
        {
            String ar_st=ar.get(i).toString().replace(" ","");
            if(ar_nm.equalsIgnoreCase(ar_st)){
                return true;
            }
        }
        return false;
    }

    private void register()
    {
        MyHelper dpHelper = new MyHelper(getApplicationContext());
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        Cursor cursor = dpHelper.alldata(cat);
        while (cursor.moveToNext()) {
            System.out.println(cursor.getString(cursor.getColumnIndex("name")));
            nameAr.add(cursor.getString(cursor.getColumnIndex("name")));
            num.add(cursor.getString(cursor.getColumnIndex("phone")));
        }
        int same=0;
        for (int i = 0; i < CustomAdapter.modelArrayList.size(); i++){
            if (contain(CustomAdapter.modelArrayList.get(i).getName(), nameAr) || contain(CustomAdapter.modelArrayList.get(i).getPhone(), num)) {
                same+=1;
                continue;
            }
            if(CustomAdapter.modelArrayList.get(i).getSelected()) {
                ContentValues insertValues = new ContentValues();
                insertValues.put("name", CustomAdapter.modelArrayList.get(i).getName());
                insertValues.put("category", cat);
                insertValues.put("phone",CustomAdapter.modelArrayList.get(i).getPhone().replace("+91","").replace(" ",""));
                long rows =db.insert("callmg", null, insertValues);
            }
        }
        if (same>0){
            Toast.makeText(getApplicationContext(), "Names or Numbers already existing in " + cat.toUpperCase(), Toast.LENGTH_SHORT)
                    .show();
        }
        dpHelper.close();
        db.close();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_select_contact);
            Intent i = getIntent();
            //Asks permission to read contact
            per_readContact();
            //Retrieve all contact list
            getContactList();
            searchc = (EditText) findViewById(R.id.searchc);
            cat = i.getExtras().getString("cat");
            lv = (ListView) findViewById(R.id.lv);
            btnselect = (Button) findViewById(R.id.select);
            btndeselect = (Button) findViewById(R.id.deselect);
            done = (Button) findViewById(R.id.next);

            modelArrayList = getModel(false);
            customAdapter = new CustomAdapter(this, modelArrayList);
            lv.setAdapter(customAdapter);

            btnselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modelArrayList = getModel(true);
                    customAdapter = new CustomAdapter(getApplicationContext(), modelArrayList);
                    lv.setAdapter(customAdapter);
                }
            });
            btndeselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modelArrayList = getModel(false);
                    customAdapter = new CustomAdapter(getApplicationContext(), modelArrayList);
                    lv.setAdapter(customAdapter);
                }
            });
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    register();
                }
            });
            searchc.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    String text = searchc.getText().toString().toLowerCase(Locale.getDefault());
                    customAdapter.filter(text);
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
        }catch (Exception e){e.printStackTrace();}
    }

    private ArrayList<Model> getModel(boolean isSelect){
        ArrayList<Model> list = new ArrayList<>();
        for(int i = 0; i < ar_name.size(); i++){
            Model model = new Model();
            model.setSelected(isSelect);
            model.setPhone(ar_phone.get(i));
            model.setName(ar_name.get(i));
            list.add(model);
        }
        return list;
    }
}

