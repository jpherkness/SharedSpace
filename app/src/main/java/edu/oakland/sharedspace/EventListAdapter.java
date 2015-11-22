package edu.oakland.sharedspace;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joseph on 11/18/15.
 */
public class EventListAdapter extends ArrayAdapter<Event>{

    public EventListAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.event_list_row, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.event_list_row, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.eventTitleTextView);
        title.setText(getItem(position).getTitle());
        title.setTextColor(Color.BLACK);

        return convertView;
    }
}
