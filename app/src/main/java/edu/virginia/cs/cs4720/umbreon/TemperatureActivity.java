package edu.virginia.cs.cs4720.umbreon;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;


public class TemperatureActivity extends ActionBarActivity {
    private final String API_KEY = "05367a7ce01bef2047689f120419a9aa";
    private final String API_URL = "http://api.openweathermap.org/data/2.5/weather?APPID=%s&%s";
    private boolean units;
    private String rpi_url;
    int[][] temp_scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        units = intent.getBooleanExtra(MainActivity.UNIT_STRING, false);
        String api_args;
        if (intent.hasExtra(MainActivity.LOC_STRING)) {
            api_args = "q=" + intent.getStringExtra(MainActivity.LOC_STRING);
        } else {
            String latitude = intent.getStringExtra(MainActivity.LAT_STRING);
            String longitude = intent.getStringExtra(MainActivity.LON_STRING);
            api_args = String.format("lat=%s&lon=%s", latitude, longitude);
        }

        if (!units) {
            api_args += "&units=imperial";
        } else {
            api_args += "&units=metric";
        }

        rpi_url = intent.getStringExtra(MainActivity.RPI_URL);

        temp_scale = new int[][]{{0, 0, 255},{0, 60, 255},{0, 104, 255},{0, 140, 255},{0, 183, 255},{0, 227, 255}, {0, 255, 255}, {0, 255, 145}, {0, 255, 108}, {0, 252, 46}, {36, 255, 0}, {79, 255, 0}, {130, 255, 0}, {170, 255, 0}, {221, 255, 0}, {255, 255, 0}, {255, 202, 0}, {255, 162, 0}, {255, 125, 0}, {255, 82, 0}, {255, 42, 0}, {255, 0, 0}};


        new GetTempTask().execute(api_args);
    }

    public String getTemp(String api_args) {
        InputStream inputStream;
        String result = "";
        String temp = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(String.format(API_URL, API_KEY, api_args));

            HttpResponse httpResponse = httpClient.execute(httpGet);

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null)
                    result += line;

                inputStream.close();
            } else {
                result = "Did not work!";
            }

            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonMain = (JSONObject) jsonObject.get("main");
            temp = jsonMain.get("temp").toString();
        } catch (Exception e) {
            Log.d("InputStream", e.toString());
        }

        return temp;
    }

    private class GetTempTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String api_args = strings[0];

            return getTemp(api_args);
        }

        @Override
        protected void onPostExecute(String temp) {
            TextView textView = (TextView) findViewById(R.id.temperature_text);
            int max_temp = 100;
            int min_temp = 32;
            try {
                double temp_double = Double.parseDouble(temp);
                textView.setText(new DecimalFormat("#.##").format(temp_double) + (units ? "C" : "F"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 175);
                if (units) temp_double = (temp_double * 1.8) + 32;
                if (temp_double < min_temp) temp_double = min_temp;
                else if (temp_double > max_temp) temp_double = max_temp;
                int index_step = ((max_temp - min_temp) / temp_scale.length);
                int color_index = (int) ((temp_double - min_temp) / index_step);
                if (color_index > temp_scale.length) color_index = temp_scale.length - 1;
                else if (color_index < 0) color_index = 0;
                Log.d("COLOR_INDEX", Integer.toString(color_index));
                int red, green, blue;
                red = temp_scale[color_index][0];
                green = temp_scale[color_index][1];
                blue = temp_scale[color_index][2];
                textView.setTextColor(Color.rgb(red, green, blue));

                new PostJSONTask().execute(rpi_url, getJSONFromRGB(red, green, blue));
            } catch (Exception e) {
                textView.setText("Error retrieving temperature");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_temperature, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PostJSONTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            String json = strings[1];

            return post(path, json);
        }

        @Override
        protected void onPostExecute(String post) {
            Log.d("HTTPRESPONSE", post);
        }
    }

    public static String post(String path, String json){
        InputStream inputStream;
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(path);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null)
                    result += line;

                inputStream.close();
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.toString());
        }

        return result;
    }

    public String getJSONFromRGB(int red, int green, int blue) {
        String JSONString = "{\"lights\": [{\"lightId\": 1, \"red\":%s,\"green\":%s,\"blue\":%s,\"intensity\":1.0}], \"propagate\": true}";
        return String.format(JSONString, red, green, blue);
    }
}
