package com.example.aneshkagoyal.covid_tracer;

import android.Manifest;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.scrounger.countrycurrencypicker.library.Buttons.CountryCurrencyButton;
import com.scrounger.countrycurrencypicker.library.Country;
import com.scrounger.countrycurrencypicker.library.CountryCurrencyPicker;
import com.scrounger.countrycurrencypicker.library.Currency;
import com.scrounger.countrycurrencypicker.library.Listener.CountryCurrencyPickerListener;
import com.scrounger.countrycurrencypicker.library.PickerType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String country_name;
    private TextView totalcases;
    private TextView recoveredcases;
    private TextView deadcases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountryCurrencyButton button = (CountryCurrencyButton) findViewById(R.id.button);
        button.setCountry("DE");
        button.setShowCurrency(false);
        totalcases = findViewById(R.id.TotalCases);
        recoveredcases = findViewById(R.id.RecoverdCases);
        deadcases = findViewById(R.id.DeadCases);
        button.setOnClickListener(new CountryCurrencyPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                if (country.getCurrency() == null) {
                    Toast.makeText(MainActivity.this,
                            String.format("name: %s\ncode: %s", country.getName(), country.getCode())
                            , Toast.LENGTH_SHORT).show();
                    country_name = country.getCode();
                    String Para= "https://api.covid19api.com/summary";
                    new LoadJSON().execute(Para);
                } else {
                    Toast.makeText(MainActivity.this,
                            String.format("name: %s\ncurrencySymbol: %s", country.getName(), country.getCurrency().getSymbol())
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSelectCurrency(Currency currency) {

            }
        });
    }
   public class LoadJSON extends AsyncTask<String,String,String>{

        //private String country_name;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Log.d("POSTEXE","ERROR");
                //Toast.makeText.this, "There was an error", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                JSONObject channel = (JSONObject) new JSONObject(response);
                JSONArray countries = channel.getJSONArray("Countries");
                for(int i=0;i<countries.length();i++){
                    JSONObject country = countries.getJSONObject(i);
                    String name =country.get("CountryCode").toString();
                    if(name.equals(country_name)){
                        totalcases.setText(country.get("TotalConfirmed").toString());
                        recoveredcases.setText(country.get("TotalRecovered").toString());
                        deadcases.setText(country.get("TotalDeaths").toString());

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


}
