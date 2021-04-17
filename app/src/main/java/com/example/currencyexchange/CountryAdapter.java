package com.example.currencyexchange;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

public class CountryAdapter extends ArrayAdapter<CountryDTO> {
    Context context;


    public CountryAdapter(Context context, ArrayList<CountryDTO> CountryDTOs) {
        super(context, R.layout.spinner_view, CountryDTOs);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);
        }

        // Lookup view for data population
        CheckedTextView tvName = (CheckedTextView) convertView.findViewById(R.id.country_name);

        // Populate the data into the template view using the data object
        CountryDTO country = getItem(position);
        tvName.setText(country.name);
        tvName.setTag(country);


        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        CheckedTextView label = (CheckedTextView) super.getDropDownView(position, convertView, parent);
        CountryDTO country = getItem(position);
        label.setText(country.name);
        return label;
    }


}