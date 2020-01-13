package com.hritik.callmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class HelpDialog {
    public void onStartDialog(Context context, String titlet, String desct, int bk,int fg,int tbk,int tfg)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_help, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView title = (TextView) promptsView
                .findViewById(R.id.help_title);
        final TextView desc = (TextView) promptsView
                .findViewById(R.id.help_desc);
        final Button ok = (Button) promptsView
                .findViewById(R.id.help_ok);
        title.setText(titlet);
        desc.setText(desct);
        LinearLayout ll= promptsView
                .findViewById(R.id.help_ll);
        LinearLayout llt= promptsView
                .findViewById(R.id.help_llt);
        ll.setBackgroundColor(ContextCompat.getColor(context,bk));
        title.setTextColor(ContextCompat.getColor(context,tfg));
        desc.setTextColor(ContextCompat.getColor(context,fg));

        ok.setTextColor(ContextCompat.getColor(context,tfg));
        ok.setBackgroundColor(ContextCompat.getColor(context,tbk));
        llt.setBackgroundColor(ContextCompat.getColor(context,tbk));

        // set dialog message
        alertDialogBuilder
                .setCancelable(false);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
