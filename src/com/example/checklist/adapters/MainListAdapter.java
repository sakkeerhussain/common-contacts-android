package com.example.checklist.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checklist.R;
import com.example.checklist.models.ListItem;

public class MainListAdapter extends ArrayAdapter<String> {
	private final Context context;
	  private final ListItem[] values;
	  private final String[] stringValues;

	  public MainListAdapter(Context context, String[] stringValues, ListItem[] values) {
	    super(context, R.layout.rowlayout, stringValues);
	    this.context = context;
	    this.stringValues = stringValues;
	    this.values = values;
	  }

	  @SuppressLint("ViewHolder")
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    textView.setText(stringValues[position]);
	    imageView.setImageResource(values[position].getImageResource());
	    return rowView;
	  }
}
