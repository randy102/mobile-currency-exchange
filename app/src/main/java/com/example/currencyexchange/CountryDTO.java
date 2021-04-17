package com.example.currencyexchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class CountryDTO implements Serializable {
    public String name;
    public String code;

    public static ArrayList<CountryDTO> fromJson(JSONObject response) throws JSONException {
        JSONArray countries = response.getJSONArray("geonames");
        ArrayList<CountryDTO> data = new ArrayList<>();
        for (int i = 0; countries.length() > i; i++) {
            try {
                JSONObject countryJson = countries.getJSONObject(i);

                CountryDTO country = new CountryDTO();
                country.name = countryJson.getString("countryName");
                country.code = countryJson.getString("currencyCode");

                data.add(country);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
