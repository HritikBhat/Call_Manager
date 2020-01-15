package com.hritik.callmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Insertion extends AppCompatActivity {
    Button submit,mcontact;
    EditText name,phone;
    String cat;
    ArrayList<String> nameAr= new ArrayList<String>();
    ArrayList<String> num= new ArrayList<String>();
    private final int PICK_CONTACT=1;
    private static void addAsContactAutomatic(final Context context, String name ,String mobile) {
        String displayName = name;
        String mobileNumber = mobile;

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        // Names
        if (displayName != null) {
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            displayName).build());
        }

        // Mobile Number
        if (mobileNumber != null) {
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        }
        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(context, "Contact " + displayName + " added.", Toast.LENGTH_SHORT)
                    .show();
        } catch (android.content.OperationApplicationException e) {
            e.printStackTrace();
            Toast.makeText(context, "Contact Not Created", Toast.LENGTH_SHORT)
                    .show();
        }
        catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(context, "Contact Not Created", Toast.LENGTH_SHORT)
                    .show();
        }

    }
    public void main_Intent(){
        Intent i2 = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i2);
    }
    protected void alert_Dialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        ask_per();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        register();
                        main_Intent();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to add in Default Contact also?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    int ask_per_count=0;
    protected void ask_per(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_CONTACTS}, 100);
            if(ask_per_count>=2){
                ask_per_count=0;
                return;
            }
            ask_per_count+=1;
            ask_per();
        }
        else{write_con();}
    }
    protected boolean ask_per_conread(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, 100);
            if(ask_per_count>=2){
                ask_per_count=0;
                return false;
            }
            ask_per_count+=1;
            ask_per_conread();
        }
        else{return true;}
        return false;
    }
    private void write_con(){
            //Writes the contact
            addAsContactAutomatic(this,name.getText().toString(),phone.getText().toString());
            register();
            main_Intent();
    }
    private void register(){
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("name", name.getText().toString());
        insertValues.put("category", cat);
        insertValues.put("phone",phone.getText().toString());
        long rows =db.insert("callmg", null, insertValues);
        System.out.println(rows);
        //Permission is being asked
    }
    public boolean contain(String st,ArrayList ar)
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
    private boolean per_readContact(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, 100);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }
        else{return true;}
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertion);
        try {
            Intent i = getIntent();
            MyHelper dpHelper = new MyHelper(this);
            SQLiteDatabase db = dpHelper.getReadableDatabase();
            cat = i.getExtras().getString("cat");
            Cursor cursor = dpHelper.alldata(cat);

            while (cursor.moveToNext()) {
                System.out.println(cursor.getString(cursor.getColumnIndex("name")));
                nameAr.add(cursor.getString(cursor.getColumnIndex("name")));
                num.add(cursor.getString(cursor.getColumnIndex("phone")));
            }
            submit = findViewById(R.id.submit);
            name = findViewById(R.id.name);
            phone = findViewById(R.id.phone);
            //contact = findViewById(R.id.contacts);
            mcontact = findViewById(R.id.mcontacts);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    char ph_first=phone.getText().toString().charAt(0);
                    if (contain(name.getText().toString(), nameAr) || contain(phone.getText().toString(), num)) {
                        Toast.makeText(getApplicationContext(), "Name or Number already existing in " + cat.toUpperCase(), Toast.LENGTH_SHORT)
                                .show();
                    }
                    else if(isEmergencyNumber(phone.getText().toString())){
                        alert_Dialog();
                    }
                    else if(ph_first=='*'){
                        alert_Dialog();
                    }
                    else if (!isNum(phone.getText().toString().trim()))
                    {
                        Toast.makeText(getApplicationContext(), "Number must contain 10 digits.", Toast.LENGTH_SHORT)
                                .show();
                    }
                    else if (name.getText().toString().trim().length()< 1)
                    {
                        Toast.makeText(getApplicationContext(), "Name is required.", Toast.LENGTH_SHORT)
                                .show();
                    }
                    else if (isNum(phone.getText().toString().trim())){
                        alert_Dialog();
                    }
                }
            });
            mcontact.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    //Here requestCode means how many contacts you can selects...
                    if(per_readContact()) {
                        Intent i = new Intent(getApplicationContext(), selectContact.class);
                        i.putExtra("cat", cat);
                        startActivity(i);
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }
    private boolean isNum(String ph){
        Pattern pattern = Pattern.compile("\\d{10}$");
        Matcher matcher = pattern.matcher(ph);
        return matcher.matches();
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

}
