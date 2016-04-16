package uk.ac.lincoln.students.gre13452104.cyclingcompanion;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class TrafficCheck extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

   // ArrayList<String> descriptions to add values to for the list
    ArrayList<String> descriptions;

    // create lat and long variables
    double lat;
    double longi;
    String incidentCount;

    private static final String TAG = "Location";
    public Location mLastLocation;
    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_check);
        descriptions = new ArrayList<String>();


        if (isConnectedToInternet()){

            if (checkGoogleServices()) {
                buildGoogleApiClient();
            }
        }
        else{
            //if an internet connection is not available, the user is warned by a message and will be redirected to the main menu
            //and the settings of the phone will be opened
            Toast.makeText(this, "Connect to the Internet!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainMenu.class);
            Intent intent2 = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
            startActivity(intent2);
        }
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
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if location can be found, then execute with the values
        //if not, add mock values
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            longi = mLastLocation.getLongitude();
            new AsyncTaskGetTraffic().execute();
        }else{
        lat = 53.77;
        longi = 0.55;
        new AsyncTaskGetTraffic().execute();
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

    public boolean isConnectedToInternet() throws SecurityException{
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
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
    // (stackoverflow.com, 2013)

    //asynchronous task to parse JSON data
    public class AsyncTaskGetTraffic extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0){

            try {
                double boxLat = lat + 0.3;
                double boxLongi = longi - 0.3;

                String Url2 = String.format("http://dev.virtualearth.net/REST/v1/Traffic/Incidents/%s,%2s,%3s,%4s?key=AgQ26fRjV0o4ZBW-6lZQElYZE71qvj5ws01zM0yVZ-M20uXHH2FI3rr_jUEMJTE2",lat,longi,boxLat,boxLongi);
                // create new instance of the httpConnect class
                httpWeather jParser = new httpWeather();
                // get json string from service url
                String json1 = jParser.getJSONFromUrl(TrafficCheck.this, Url2);
                JSONArray incidents = (new JSONObject(json1)).getJSONArray("resourceSets");
                int q = 0;
               incidentCount = incidents.getJSONObject(q).getString("estimatedTotal");
                descriptions.add("Total number of incidents: " + incidentCount);
                JSONArray getIncidents = incidents.getJSONObject(q).getJSONArray("resources");

                for( int i = 0; i < getIncidents.length(); ++i) {
                   descriptions.add("Incident :" + getIncidents.getJSONObject(i).getString("description") );
                }
            }
            catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
         //below method will run when service HTTP request is complete, will then bind traffic text in descriptions to ListTraffic
        protected void onPostExecute(String strFromDoInBg) {
            ListView list = (ListView)findViewById(R.id.listTraffic);
            View root = list.getRootView();
            //get number of values and change background colour dependent on what they are
            int incCount = Integer.parseInt(incidentCount);
            if(incCount >= 20)
            {
                //set background to red
                root.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                descriptions.add("High number of incidents, beware!");
            }
            else{
                //set background to green
                root.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                descriptions.add("Seems safe to cycle, but exercise caution!");
            }
            //attach values to an adapter
            ArrayAdapter<String> trafficArrayAdapter = new ArrayAdapter<String>(TrafficCheck.this, android.R.layout.simple_expandable_list_item_1, descriptions);
            //set values to list
            list.setAdapter(trafficArrayAdapter);
            //(derekfoster.cloudapp.net)
        }
    }


}
