package uk.ac.lincoln.students.gre13452104.cyclingcompanion;

/**
 * Created by luke on 02/12/2015.
 */

    public class Weather {

        int _id;
        Double _temperature;
        int _wind_speed;

        // Empty constructor
        public Weather(){

        }
        // constructor
        public Weather(int id, Double temperature, int _wind_speed){
            this._id = id;
            this._temperature = temperature;
            this._wind_speed = _wind_speed;
        }

        // constructor
        public Weather(Double temperature, int _wind_speed){
            this._temperature = temperature;
            this._wind_speed = _wind_speed;
        }
        // getting id
        public int getID(){
            return this._id;
        }

        // setting id
        public void setID(int id){

            this._id = id;
        }

        // getting temp
        public Double getTemp(){

            return this._temperature;
        }

        // setting temp
        public void setTemp(Double temperature){
            this._temperature = temperature;
        }

        // getting wind speed
        public int getWindSpeed(){
            return this._wind_speed;
        }

        // setting wind speed
        public void setWindSpeed(int wind_speed){
            this._wind_speed = wind_speed;
        }
    }

//(Androidhive.com, 2015)

