package coding.legaspi.caviteuser.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun getTimeMiles(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        val maxDayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar[Calendar.DAY_OF_MONTH] = Math.min(day, maxDayCount)
        return calendar.timeInMillis
    }
    fun getCurrentTime(): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }
    fun getMonthDayCount(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    fun getDay(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return calendar[Calendar.DAY_OF_MONTH]
    }
    fun getMonth(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return calendar[Calendar.MONTH]
    }
    fun getYear(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return calendar[Calendar.YEAR]
    }
    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        return year
    }
    fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        return month
    }
    fun getCurrentDay(): Int {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return day
    }
    fun getCurrentHour(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.HOUR_OF_DAY]
    }
    fun getCurrentMinute(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.MINUTE]
    }
    fun getCurrentTimePeriod(): String {
        val calendar = Calendar.getInstance()
        return if (calendar[Calendar.AM_PM] == Calendar.AM) {
            "AM"
        } else {
            "PM"
        }
    }
    fun formatTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
    fun formatTimestampToTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
    fun getCurrentTimestamp(): String {
        return System.currentTimeMillis().toString()
    }
    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd yyyy, hh:mm a", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }

    fun getHour(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return calendar[Calendar.HOUR_OF_DAY] // Returns the hour in 24-hour format
    }

    fun getMinutes(timeStamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return calendar[Calendar.MINUTE]
    }

    fun parseDateToTimestamp(dateString: String): Long {
        // Define the date format pattern
        val sdf = SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.getDefault())

        return try {
            // Remove any leading or trailing spaces
            val cleanedDateString = dateString.trim()

            // Parse the date string
            val date: Date? = sdf.parse(cleanedDateString)

            // Log the parsed date for debugging
            println("Parsed Date: $date")

            // Return the timestamp in milliseconds
            date?.time ?: 0L // Return 0 if parsing fails
        } catch (e: ParseException) {
            // Log the error and the input string for debugging
            println("Error parsing date string: $dateString")
            e.printStackTrace()
            0L // Return 0 if parsing fails
        }
    }


}