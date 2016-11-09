package weather.com.weatherview.model;

/**
 * Created by gerson on 10/6/16.
 */

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Weather {

    public String dayOfWeek;
    public String minTemp;
    public String maxTemp;
    public String humidity;
    public String description;
    public String iconURL;


    /**
     * Constructor of class
     *
     * \u00B0 represents the degree symbol º
     * @param timeStamp
     * @param minTemp
     * @param maxTemp
     * @param humidity
     * @param description
     * @param iconName
     */
    public Weather(long timeStamp, double minTemp, double maxTemp,
                   double humidity, String description, String iconName) {
        java.text.NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + "\u00B0"+"F";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0"+"F";
        this.humidity = NumberFormat.getPercentInstance()
                .format(humidity/100.0);
        this.description = description;
        this.iconURL =  "http://openweathermap.org/img/w/" + iconName + ".png";
    }

    /**
     * Convert to a day's name
     * @param timeStamp
     * @return
     */
    private static String convertTimeStampToDay(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp*1000);
        TimeZone timeZone = TimeZone.getDefault();

        calendar.add(Calendar.MILLISECOND,
                timeZone.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");

        return simpleDateFormat.format(calendar.getTime());
    }
}
