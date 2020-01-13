package com.hritik.callmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewCat extends AppCompatActivity {

    Button create;
    EditText cate;
    Context c = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_new_cat);
            cate = findViewById(R.id.cate);
            create = findViewById(R.id.create_cat);

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyHelper dpHelper = new MyHelper(getApplicationContext());
                    SQLiteDatabase db = dpHelper.getReadableDatabase();
                    Cursor cursor = dpHelper.getCategoryNames();
                    int count_row = 0;
                    while (cursor.moveToNext()) {

                        final String cat_name = cursor.getString(cursor.getColumnIndex("cname"));
                        if (cat_name.equalsIgnoreCase(cate.getText().toString())) {
                            Toast.makeText(c, "This group name is already exists!!", Toast.LENGTH_SHORT)
                                    .show();
                            count_row++;
                            return;
                        }
                        count_row++;
                    }
                    if (cate.getText().toString().trim().length() < 1) {
                        Toast.makeText(c, "Name is required!!!", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    if (count_row >= 10) {
                        Toast.makeText(c, "Group Limit! Max 10", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("cname", cate.getText().toString());
                        long rows = db.insert("catnm", null, insertValues);
                        Toast.makeText(getApplicationContext(),"Group Created Successfully",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }
                }
            });
        }
        catch (Exception e)
        {e.printStackTrace();}
    }
}
