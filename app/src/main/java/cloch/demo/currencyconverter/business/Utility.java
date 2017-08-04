package cloch.demo.currencyconverter.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Chhorvorn on 8/2/2017.
 */

public class Utility
{
    public static Date truncateTime(Date value)
    {
        GregorianCalendar date = new GregorianCalendar(TimeZone.getDefault());
        date.setTime(value);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }
}
