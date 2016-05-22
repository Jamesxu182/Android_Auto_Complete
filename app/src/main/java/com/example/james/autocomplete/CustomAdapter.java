package com.example.james.autocomplete;

/**
 * Created by James on 15/11/21.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

/**
 * Created by James on 15/11/8.
 */
public class CustomAdapter extends ArrayAdapter<Item> {
    public CustomAdapter(Context context, ArrayList<Item> custom_row) {
        super(context, 0, custom_row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Item item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_layout_item, parent, false);
        }
        // Lookup view for data population
        TextView text_view = (TextView) convertView.findViewById(R.id.text);
        ImageView image_view = (ImageView) convertView.findViewById(R.id.image);
        ViewSwitcher switcher = (ViewSwitcher)convertView.findViewById(R.id.viewSwitcher);
        text_view.setText(item.getContent());

        switch(item.getSource()) {
            case 1:
                image_view.setImageResource(R.drawable.database);
                break;
            case 2:
                image_view.setImageResource(R.drawable.google);
                break;
            case 3:
                image_view.setImageResource(R.drawable.place);
                break;
            default:
                break;
        }

        return convertView;
    }
}

