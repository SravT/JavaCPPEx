package edu.virginia.cs.cs4720.umbreon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MainActivity extends ActionBarActivity {
    public final static String RPI_URL = "edu.virginia.cs.cs4720.umbreon.MESSAGE";
    public final static String JSON_STRING = "edu.virginia.cs.cs4720.umbreon.MESSAGE2";
    public final static String LAT_STRING = "edu.virginia.cs.cs4720.umbreon.LATITUDE";
    public final static String LON_STRING = "edu.virginia.cs.cs4720.umbreon.LONGITUDE";
    public final static String LOC_STRING = "edu.virginia.cs.cs4720.umbreon.LOC_STRING";
    public final static String UNIT_STRING = "edu.virginia.cs.cs4720.umbreon.UNITS";

    private LocationManager locationManager;
    private String locationProvider;
    private double latitude, longitude;
    private LocationListener locationListener;
    private TextView gpsText;
    private ListView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        locationView = (ListView) findViewById(R.id.location_view);
        Set<String> savedLocationsSet = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getStringSet("savedLocations", new HashSet<String>());
        String[] savedLocations = savedLocationsSet.toArray(new String[savedLocationsSet.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, savedLocations);
        locationView.setAdapter(adapter);
        locationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedLocation = (String) locationView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), TemperatureActivity.class);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String default_url = "http://" + sharedPreferences.getString("pref_syncURL", "") + "/rpi";
                intent.putExtra(RPI_URL, default_url);
                intent.putExtra(LOC_STRING, clickedLocation);
                intent.putExtra(UNIT_STRING, sharedPreferences.getBoolean("pref_units", false));
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager.getBestProvider(new Criteria(), false);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locationManager.removeUpdates(this);
                startTemperatureActivity(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        gpsText = (TextView) findViewById(R.id.gpsCoords);
    }

    public void startTemperatureActivity(double latitude, double longitude) {
        Intent intent = new Intent(this, TemperatureActivity.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String default_url = "http://" + sharedPreferences.getString("pref_syncURL", "") + "/rpi";
        intent.putExtra(RPI_URL, default_url);
        intent.putExtra(LAT_STRING, Double.toString(latitude));
        intent.putExtra(UNIT_STRING, sharedPreferences.getBoolean("pref_units", false));
        intent.putExtra(LON_STRING, Double.toString(longitude));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendJSON(String json) {
        Intent intent = new Intent(this, PostActivity.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String default_url = "http://" + sharedPreferences.getString("pref_syncURL", "") + "/rpi";
        Log.i("Default URL", default_url);
        Log.i("JSON", json);
        intent.putExtra(RPI_URL, default_url);
        intent.putExtra(JSON_STRING, json);
        startActivity(intent);
    }

    public void sendBlue(View view) {
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject light1 = new JSONObject();
            light1.accumulate("lightId", 1);
            light1.accumulate("red", 0);
            light1.accumulate("green", 0);
            light1.accumulate("blue", 255);
            light1.accumulate("intensity", 0.7);

            JSONArray lights = new JSONArray();
            lights.put(light1);

            jsonObject.accumulate("lights", lights);
            jsonObject.accumulate("propagate", true);

            json = jsonObject.toString();
        } catch (Exception e) {
            Log.d("JSON", e.getLocalizedMessage());
        }
        sendJSON(json);
    }

    public void sendOrange(View view) {
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject light1 = new JSONObject();
            light1.accumulate("lightId", 1);
            light1.accumulate("red", 255);
            light1.accumulate("green", 128);
            light1.accumulate("blue", 0);
            light1.accumulate("intensity", 0.7);

            JSONArray lights = new JSONArray();
            lights.put(light1);

            jsonObject.accumulate("lights", lights);
            jsonObject.accumulate("propagate", true);

            json = jsonObject.toString();
        } catch (Exception e) {
            Log.d("JSON", e.getLocalizedMessage());
        }
        sendJSON(json);
    }

    public void getGPSCoordinates(View view) {
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        gpsText.setText("Waiting for GPS coordinates (may take a while)...");
    }

    public void startAddLocation(View view) {
        Intent intent = new Intent(this, AddLocationActivity.class);
        startActivity(intent);
    }
}
