package com.hritik.callmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button family,friends,services,office;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        family = findViewById(R.id.family);
        friends = findViewById(R.id.friends);
        services = findViewById(R.id.services);
        office = findViewById(R.id.office);

        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Section.class);
                i.putExtra("cat","Family");
                startActivity(i);
            }
        });
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Section.class);
                i.putExtra("cat","Friends");
                startActivity(i);
            }
        });
        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Section.class);
                i.putExtra("cat","Services");
                startActivity(i);
            }
        });
        office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Section.class);
                i.putExtra("cat","Office");
                startActivity(i);

            }
        });
    }
}
