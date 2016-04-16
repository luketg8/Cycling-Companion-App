package uk.ac.lincoln.students.gre13452104.cyclingcompanion;

import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.Toast;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;


public class WeatherCheck extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Array List which will store every item that will be passed to the list
    ArrayList<String> items;

    //create temp and wind variables, for ease of adding to database
    double temp;
    int wind;

    //set variables for latitude and longitude
    double lat;
    double longi;

    //Create an instance of the SQLite Database
    DatabaseMaintenance db = new DatabaseMaintenance(this);

    private static final String TAG = "Location";
    public Location mLastLocation;
    public GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_check);

        items = new ArrayList<String>();

        if (isConnectedToInternet()){
                if (checkGoogleServices()) {
                buildGoogleApiClient();
            }
        }
        else{
            //if an internet connection is not available, the user is warned by a message and will be redirected to the main menu
            //and the settings of the phone will be opened
            Toast.makeText(this, "Connect to the Internet!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainMenu.class);
            Intent intent2 = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
            startActivity(intent2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent1 = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public boolean checkGoogleServices()
    {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if location can be found, then execute with the values
        //if not, add mock values
        if(mLastLocation != null){
            lat = mLastLocation.getLatitude();
            longi = mLastLocation.getLongitude();
            new AsyncTaskGetWeather().execute();
        }else {
            lat = 53.77;
            longi = 0.55;
            new AsyncTaskGetWeather().execute();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.i(TAG, "Failed: " + connectionResult.getErrorMessage());
    }
    //(developers.android.com, 2015)

    public boolean isConnectedToInternet() throws SecurityException{
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            //get network information
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
    //(stackoverflow.com, 2013)

//asynchronous task to parse JSON data
    public class AsyncTaskGetWeather extends AsyncTask<String, String, String>{

   @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0){

        try {
            //call weather API with the latitude and longitude of the user
            String Url1 = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%2s&units=metric", lat, longi);

            // create new instance of the httpConnect class
            httpWeather jParser = new httpWeather();

            // get json string from service url
            String json = jParser.getJSONFromUrl(WeatherCheck.this, Url1);

            String location = (new JSONObject(json).getString("name"));
            //display the name of the current location
            items.add(location);

            //access weather array from within the object
            JSONArray forecast = (new JSONObject(json)).getJSONArray("weather");
            int inte = 0;
            items.add("This is the Current Forecast :");
            //hget string from weather array
            items.add("Outlook: " + forecast.getJSONObject(inte).getString("description"));

            // create an instance of an JSONObject to parse the object in the JSON
            JSONObject jsonObject = new JSONObject(json).getJSONObject("main");

            // get values of temperature, cloud count and wind speed and add them to the list
            temp = (jsonObject.getDouble("temp"));
            items.add("Temperature:\t"+temp+" Degrees Celsius");
            JSONObject jsonObject1 = new JSONObject(json).getJSONObject( "clouds" );
            items.add("Number of Clouds:\t"+jsonObject1.getInt("all"));
            JSONObject jsonObject2 = new JSONObject(json).getJSONObject("wind");
            wind = (jsonObject2.getInt("speed"));
            items.add("Wind Speed:\t"+wind+" mph");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    // below method will run when service HTTP request is complete, will then bind text in arrayList to listWeather
    protected void onPostExecute(String strFromDoInBg) {
        // Inserting Weather info
        Log.d("Insert: ", "Inserting ..");
        db.addWeatherInfo(new Weather(temp, wind));

        // Reading all weather
        Log.d("Reading: ", "Reading all weather..");
        List<Weather> entries = db.getAllWeather();

        for (Weather w : entries) {
            String log = "Id: "+w.getID()+" ,Temp: " + w.getTemp() + " ,Wind Speed: " + w.getWindSpeed();
            // Writing weather information to log
            Log.d("Temp: ", log);
        }
        // change the colour of the background dependent on the temperature
        View someView = findViewById(R.id.listWeather);
        View root = someView.getRootView();
        if (temp >= 30)
        {
            //set background value
            root.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            //add description to the list
            items.add("The background has been made red, meaning it is considered to be unsafe to cycle");
        }
        else if (temp >= 10)
        {
            root.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            items.add("The background has been made green, meaning it is considered to be safe to cycle");
        }
        else if (temp < 10)
        {
            root.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            items.add("The background has been made blue, meaning it would be wise to exercise caution when cycling");
        }
        ListView list = (ListView)findViewById(R.id.listWeather);
        ArrayAdapter<String> weatherArrayAdapter = new ArrayAdapter<String>(WeatherCheck.this, android.R.layout.simple_expandable_list_item_1, items);
        //add values to the list
        list.setAdapter(weatherArrayAdapter);
    }


}
}