package uk.ac.lincoln.students.gre13452104.cyclingcompanion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luke on 02/12/2015.
 */
public class DatabaseMaintenance extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "WeatherData";

    // Contacts table name
    private static final String TABLE_WEATHER = "weather";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "temperature";
    private static final String KEY_WIND = "WindSpeed";

    public DatabaseMaintenance(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WEATHER_TABLE = "CREATE TABLE " + TABLE_WEATHER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_WIND + " TEXT" + ")";
        db.execSQL(CREATE_WEATHER_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);

        // Create tables again
        onCreate(db);
    }

    // Adding new weather entry
    void addWeatherInfo(Weather weather) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, weather.getTemp()); // Contact Name
        values.put(KEY_WIND, weather.getWindSpeed()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_WEATHER, null, values);
        db.close(); // Closing database connection
    }

    // Getting single weather entry
    Weather getWeather(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WEATHER, new String[] { KEY_ID,
                        KEY_NAME, KEY_WIND }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Weather weather = new Weather(Integer.parseInt(cursor.getString(0)),
                cursor.getDouble(1), cursor.getInt(2));
        // return contact
        return weather;
    }

    // Getting all weather entries
    public List<Weather> getAllWeather() {
        List<Weather> weatherList = new ArrayList<Weather>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WEATHER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Weather weather = new Weather();
                weather.setID(Integer.parseInt(cursor.getString(0)));
                weather.setTemp(cursor.getDouble(1));
                weather.setWindSpeed(cursor.getInt(2));
                // Adding contact to list
                weatherList.add(weather);
            } while (cursor.moveToNext());
        }

        // return weather list
        return weatherList;
    }

//(Androidhive.com, 2015)


}
