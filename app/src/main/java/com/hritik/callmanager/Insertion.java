package com.hritik.callmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class Insertion extends AppCompatActivity {
    Button submit;
    EditText name,phone;
    RadioGroup grp;
    String selRadio;

    private void addContact(String name, String phone) {

        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER,phone);
        Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = getContentResolver().insert(updateUri, values);
        Log.d("CONTACT", ""+updateUri);
    }
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
        insertValues.put("category", selRadio);
        insertValues.put("phone", phone.getText().toString());
        long rows =db.insert("callmg", null, insertValues);
        System.out.println(rows);
        //Permission is being asked
    }
    public RadioButton getObj(int radio_id){
        grp.check(radio_id);
        return (RadioButton)grp.findViewById(radio_id);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertion);
        Intent i=getIntent();
        String cat = i.getExtras().getString("cat");
        submit=findViewById(R.id.submit);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        grp=findViewById(R.id.grp);
        switch(cat){
            case "Office":
                selRadio= getObj(R.id.office).getText().toString();
                break;
            case "Services":
                selRadio= getObj(R.id.services).getText().toString();
                break;
            case "Friends":
                selRadio= getObj(R.id.friends).getText().toString();
                break;
            case "Family":
                selRadio= getObj(R.id.family).getText().toString();
                break;
        }

        grp.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override
                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId)
                    {

                        // Get the selected Radio Button
                        RadioButton
                                radioButton
                                = (RadioButton)group
                                .findViewById(checkedId);
                        selRadio= radioButton.getText().toString();
                    }
                });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_Dialog();
                /*
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, name.getText().toString());
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone.getText().toString());
                startActivity(intent);*/
            }
        });
    }
}
