package uk.ac.lincoln.students.gre13452104.cyclingcompanion;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.*;

public class MainMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //apply animation to the main heading
        YoYo.with(Techniques.Landing)
                .duration(2500)
                .playOn(findViewById(R.id.textView));

        //read in the images to be loaded using Picasso
        ImageView imageView = (ImageView) findViewById(R.id.imageButton);

        ImageView imageView2 = (ImageView) findViewById(R.id.imageButton2);

        Picasso.with(this)
                .load("http://shmector.com/_ph/9/97739585.png")
                //(schmector.com, 2015)
                .resize(125, 125)
                .centerCrop()
                .into(imageView);

        Picasso.with(this)
                .load("https://pixabay.com/static/uploads/photo/2014/11/03/10/10/traffic-lights-514932_960_720.jpg")
                        //(pixabay.com, 2015)
                .resize(125, 125)
                .centerCrop()
                .into(imageView2);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent1 = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user touches the button */
    public void getWeather(View view) {

            // create the intent which starts the Weather activity
            Intent intent = new Intent(this, WeatherCheck.class);
            startActivity(intent);

    }

    public void getTraffic(View view) {

        // create the intent which starts the Weather activity
        Intent intent = new Intent(this, TrafficCheck.class);
        startActivity(intent);
    }

}
