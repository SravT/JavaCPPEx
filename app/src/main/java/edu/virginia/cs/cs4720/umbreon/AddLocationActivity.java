package edu.virginia.cs.cs4720.umbreon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;


public class AddLocationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_location, menu);
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

    public void submitLocation(View view) {
        String cityName = ((EditText) findViewById(R.id.city_text)).getText().toString();
        String stateAbbr = ((EditText) findViewById(R.id.state_text)).getText().toString();
        SharedPreferences savedLocationsPrefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = savedLocationsPrefs.edit();
        editor.clear();
        Set<String> savedLocations = savedLocationsPrefs.getStringSet("savedLocations", new HashSet<String>());
        savedLocations.add(cityName + "," + stateAbbr);
        editor.putStringSet("savedLocations", savedLocations);
        editor.commit();
        NavUtils.navigateUpFromSameTask(this);
    }
}
