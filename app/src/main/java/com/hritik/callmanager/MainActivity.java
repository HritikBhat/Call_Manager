package com.hritik.callmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Button family,friends,services,office;
    ArrayList <Button> category= new ArrayList<Button>();
    ArrayList<Integer> lt = new ArrayList<Integer>();
    LinearLayout ll;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        lt.add(R.id.lt_1);
        lt.add(R.id.lt_2);
        lt.add(R.id.lt_3);
        lt.add(R.id.lt_4);
        lt.add(R.id.lt_5);
        Cursor cursor =dpHelper.getCategoryNames();
        int i=0;
        int lt_counter=-1;
        while(cursor.moveToNext()) {
            final String cat_name=cursor.getString(cursor.getColumnIndex("cname"));
            System.out.println(cat_name);
            category.add(new Button(this));
            category.get(i).setTag("btn_"+cat_name);
            category.get(i).setText(cat_name);
            category.get(i).setBackgroundColor(getResources().getColor(R.color.taskbarcolor));
            System.out.println(i);
            //category.get(i).setHeight(358);
            //category.get(i).setWidth(362);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            //Toast.makeText(this, "Height: "+height+"  Width"+width, Toast.LENGTH_SHORT)
              //      .show();
            category.get(i).setHeight((int)(height/5));
            category.get(i).setWidth((int)(width/3));
            category.get(i).setPadding(10,10,10,10);
            category.get(i).setTextSize(16);
            //1184 x 720
            //category.get(i).setPadding(20);
            //category.get(i).setTypeface(Typeface.DEFAULT_BOLD);
            category.get(i).setTextColor(Color.parseColor("#F3F1F2"));

            if(i%2==0){
                lt_counter+=1;
                ll = (LinearLayout)findViewById(lt.get(lt_counter));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                //left,top,right,bottom
                lp.setMargins(0, 0, 50, 0);
                ll.addView(category.get(i), lp);
            }
            else{
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                ll.addView(category.get(i), lp);
            }
            category.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),Section.class);
                    i.putExtra("cat",cat_name);
                    startActivity(i);
                }
            });
            i+=1;
        }

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.addcat);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),NewCat.class);
                startActivity(i);
            }
        });
    }
}
