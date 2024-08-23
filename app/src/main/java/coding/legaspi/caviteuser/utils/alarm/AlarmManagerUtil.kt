package coding.legaspi.caviteuser.utils.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import coding.legaspi.caviteuser.data.local.ItineraryEntity

object AlarmManagerUtil {
    fun setAlarm(context: Context, alarm: ItineraryEntity): Boolean  {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.i("CUItineraryActivity", "Alarm ${alarm.id.toInt()}")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarm.id.toInt())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return try {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.scheduleDateTimestamp, pendingIntent)
                Toast.makeText(context, "Alarm set for ${alarm.scheduleDateTimestamp}", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Log.e("AlarmManager", "Failed to set alarm", e)
            }

            Toast.makeText(context, "Alarm set for ${alarm.scheduleDateTimestamp}", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to set alarm: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("CUItineraryActivity", "Alarm Error $e")
            false
        }
    }

    fun isAlarmSet(context: Context, alarmId: Int): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarmId, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent != null
    }

    fun cancelAlarm(context: Context, alarm: ItineraryEntity): Boolean  {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
//            Toast.makeText(context, "Alarm canceled", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to cancel alarm: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}