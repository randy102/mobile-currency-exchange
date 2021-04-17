package com.example.currencyexchange;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> data;
    String fromCode;
    String toCode;
    EditText fromValue;
    TextView toValue;
    TextView rate;
    double rateValue;

    RSSParser rssParser = new RSSParser();
    List<RSSItem> rssItems = new ArrayList<>();
    private static String TAG_DES = "description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<>();
        fromValue = findViewById(R.id.from_value);
        toValue = findViewById(R.id.to_value);
        rate = findViewById(R.id.rate);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.geonames.org/countryInfo?username=randy102&type=JSON";
        JsonObjectRequest stringRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            updateView(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(stringRequest);

        Button convertBtn = findViewById(R.id.convert);
        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadRSSFeedItems().execute("https://"+fromCode+".fxexchangerate.com/"+toCode+".xml");
            }
        });
    }

    public void updateView(JSONObject response) throws JSONException {
        ArrayList<CountryDTO> countries = CountryDTO.fromJson(response);

        CountryAdapter fromAdapter = new CountryAdapter(this, countries);
        Spinner fromSpinner = (Spinner) findViewById(R.id.from_spinner);
        fromSpinner.setAdapter(fromAdapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CountryDTO country = (CountryDTO) parent.getItemAtPosition(position);
                fromCode = country.code.toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fromCode = null;
            }
        });

        CountryAdapter toAdapter = new CountryAdapter(this, countries);
        Spinner toSpinner = (Spinner) findViewById(R.id.to_spinner);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CountryDTO country = (CountryDTO) parent.getItemAtPosition(position);
                toCode = country.code.toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toCode = null;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void convertCurrency() {
        String fromValueStr = fromValue.getText().toString();
        if (!fromValueStr.isEmpty()){
            double src = Double.parseDouble(fromValueStr);
            double result = rateValue * src;
            toValue.setText(Double.toString(result));
        }
    }


    public class LoadRSSFeedItems extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            String rss_url = args[0];
            rssItems = rssParser.getRSSFeedItems(rss_url);
            runOnUiThread(new Runnable() {
                public void run() {
                    for (RSSItem item: rssItems){
                        String ratePattern = "=\\s(.+)\\s"+ toCode.toUpperCase() +"<br";
                        String rateTextPattern = "\\s+(.*)<br\\/>";

                        Pattern rateP = Pattern.compile(ratePattern);
                        Pattern rateText = Pattern.compile(rateTextPattern);

                        // Now create matcher object.
                        Matcher rateMatch = rateP.matcher(item.description);
                        Matcher rateTextMatch = rateText.matcher(item.description);

                        if (rateMatch.find() && rateTextMatch.find()) {
                            rate.setText(rateTextMatch.group(1));
                            rateValue = Double.parseDouble(Objects.requireNonNull(rateMatch.group(1)));
                            convertCurrency();
                        }
                    }
                }
            });
            return null;
        }

    }
}