package cloch.demo.currencyconverter.business

import java.util.*

/**
 * Created by Chhorvorn on 8/4/2017.
 */

fun truncateTime(value: Date): Date
{
    val date = GregorianCalendar(TimeZone.getDefault())
    date.time = value
    date.set(Calendar.HOUR_OF_DAY, 0)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    return date.time
}