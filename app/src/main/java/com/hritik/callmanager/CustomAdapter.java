package com.hritik.callmanager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hardik on 9/1/17.
 */
public class CustomAdapter  extends BaseAdapter {

    private Context context;
    public static ArrayList<Model> modelArrayList;
    private ArrayList<Model> arraylist=null;


    public CustomAdapter(Context context, ArrayList<Model> modelArrayList) {

        this.context = context;
        this.modelArrayList = modelArrayList;
        this.arraylist = new ArrayList<Model>();
        this.arraylist.addAll(modelArrayList);

    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        modelArrayList.clear();
        if (charText.length() == 0) {
            modelArrayList.addAll(arraylist);
        }
        else
        {
            for (Model wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    modelArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
            holder.Name= (EditText) convertView.findViewById(R.id.name_c);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }


        //holder.checkBox.setText("Checkbox "+position);
        holder.Name.setText(modelArrayList.get(position).getName());

        holder.checkBox.setChecked(modelArrayList.get(position).getSelected());

        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                EditText tv = (EditText) tempview.findViewById(R.id.name_c);
                Integer pos = (Integer)  holder.checkBox.getTag();
                Toast.makeText(context, "Checkbox "+pos+" clicked!", Toast.LENGTH_SHORT).show();

                if(modelArrayList.get(pos).getSelected()){
                    modelArrayList.get(pos).setSelected(false);
                }else {
                    modelArrayList.get(pos).setSelected(true);
                }

            }
        });
        holder.Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                modelArrayList.get(position).setEditTextValue(holder.Name.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return convertView;
    }

    private class ViewHolder {

        protected CheckBox checkBox;
        private EditText Name;

    }

}
