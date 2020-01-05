package com.hritik.callmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewCat extends AppCompatActivity {

    Button create;
    EditText cate;
    Context c = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cat);

        cate = findViewById(R.id.cate);
        create = findViewById(R.id.create_cat);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cate.getText().toString().length()<1){
                    Toast.makeText(c, "Name is required!!!", Toast.LENGTH_SHORT)
                            .show();}
                else{
                MyHelper dpHelper = new MyHelper(c);
                SQLiteDatabase db = dpHelper.getReadableDatabase();
                ContentValues insertValues = new ContentValues();
                insertValues.put("cname", cate.getText().toString());
                long rows =db.insert("catnm", null, insertValues);
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);}
            }
        });

    }
}
