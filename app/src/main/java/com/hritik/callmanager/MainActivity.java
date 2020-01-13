package com.hritik.callmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Button family,friends,services,office;
    final Context context = this;
    ArrayList <Button> category= new ArrayList<Button>();
    ArrayList<Integer> lt = new ArrayList<Integer>();
    LinearLayout ll;

    public boolean checkNamesFromD(EditText cate){
        MyHelper dpHelper = new MyHelper(getApplicationContext());
        Cursor cursor = dpHelper.getCategoryNames();
        while (cursor.moveToNext()) {
            final String cat_name = cursor.getString(cursor.getColumnIndex("cname"));
            if (cat_name.equalsIgnoreCase(cate.getText().toString())) {
                Toast.makeText(context, "This group name is already exists!!", Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        }
        return false;
    }

    public void refresh_Page(){
        try{
        for(int i=0;i<lt.size();i++){
            LinearLayout lyt = (LinearLayout) findViewById(lt.get(i));
            lyt.removeAllViews();
        }}
        catch (Exception e){e.printStackTrace();}
        category.clear();
        lt.clear();
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
        while(cursor.moveToNext()){
            final int i2=i;
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

            //On long press
            category.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(MainActivity.this, category.get(i2));
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.edit) {

                                //Creation of dialog
                                LayoutInflater li = LayoutInflater.from(context);
                                View promptsView = li.inflate(R.layout.dialog_new_name, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        context);

                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(promptsView);

                                final EditText userInput = (EditText) promptsView
                                        .findViewById(R.id.editNewName);

                                // set dialog message
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        // get user input and set it to result
                                                        // edit text
                                                        if(checkNamesFromD(userInput)){
                                                            return;
                                                        }
                                                        else if (userInput.getText().toString().trim().length() < 1) {
                                                            Toast.makeText(getApplicationContext(), "Name is required!!!", Toast.LENGTH_SHORT)
                                                                    .show();
                                                            return;
                                                        }
                                                        else {
                                                            edit_grp_name(category.get(i2).getText().toString(),userInput.getText().toString());
                                                            refresh_Page();
                                                            Toast.makeText(getApplicationContext(),"Edit Successfully",Toast.LENGTH_SHORT).show();
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
                            } else if (item.getItemId() == R.id.delete) {
                                delete_grp(category.get(i2).getText().toString());
                                refresh_Page();
                                Toast.makeText(getApplicationContext(),"Delete Successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                return false;
                            }
                            return true;
                        }
                    });
                    popup.show();
                    return true;
                }});
            i+=1;
        }

    }



    public void delete_grp(String cat){
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getWritableDatabase();
        //Error cause database delete
        db.execSQL("DELETE FROM callmg WHERE category='"+cat+"'");
        db.execSQL("DELETE FROM catnm WHERE cname='"+cat+"'");
        System.out.println("Delete GRP");
        db.close();
        dpHelper.close();
        refresh_Page();
    }
    public void edit_grp_name(String cat,String new_name){
        MyHelper dpHelper = new MyHelper(this);
        SQLiteDatabase db = dpHelper.getWritableDatabase();
        //Error cause database delete
        db.execSQL("UPDATE callmg SET category='"+new_name+"' "+"WHERE category='"+cat+"'");
        db.execSQL("UPDATE catnm SET cname='"+new_name+"' "+"WHERE cname='"+cat+"'");
        System.out.println("Edit GRP Name");
        db.close();
        dpHelper.close();
        refresh_Page();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh_Page();
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.addcat);
        FloatingActionButton helpFab = (FloatingActionButton) findViewById(R.id.help);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),NewCat.class);
                startActivity(i);
            }
        });
        helpFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelpDialog d = new HelpDialog();
                final int bk=R.color.black2;
                final int fg=R.color.white;
                final int tfg=R.color.white;
                final int tbk=R.color.taskbarcolor;

                String title="Home Page Help";
                String desc="1)To delete or edit group name press on the group button for few seconds to receive popup menu.\n\n" +
                        "2)For creation of group,click on + symbol button on the bottom right side.";
                d.onStartDialog(context,title,desc,bk,fg,tbk,tfg);
            }
        });
    }

}
