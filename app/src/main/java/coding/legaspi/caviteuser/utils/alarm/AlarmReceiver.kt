package coding.legaspi.caviteuser.utils.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.local.ItineraryDatabase
import coding.legaspi.caviteuser.data.local.ItineraryEntity
import coding.legaspi.caviteuser.presentation.home.event.ViewEventActivity
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import kotlinx.coroutines.runBlocking

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received at ${System.currentTimeMillis()}")
        val alarmId = intent.getIntExtra("alarm_id", -1)
        Log.d("AlarmReceiver", "onReceive alarm_id ${alarmId}")
        if (alarmId != -1) {
            val alarm = getAlarmById(context, alarmId) // Retrieve alarm details if needed
            alarm?.let {
                Log.d("AlarmReceiver", "getAlarmById ${alarm}")
                showNotification(context, it)
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        Log.d("AlarmReceiver", "createNotificationChannel ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_channel"
            val channelName = "Alarm Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for Alarm notifications"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun getAlarmById(context: Context, alarmId: Int): ItineraryEntity? {
        val db = ItineraryDatabase.getDatabase(context)
        val alarmDao = db.itineraryDao()
        // Ideally, this should be done in a coroutine or another async mechanism
        return runBlocking { alarmDao.getItineraryById(alarmId) }
    }

    private fun showNotification(context: Context, alarm: ItineraryEntity) {
        Log.d("AlarmReceiver", "showNotification ${alarm}")
        createNotificationChannel(context)
        updateFirebase(context, alarm)
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notificationIntent = Intent(context, ViewEventActivity::class.java).apply {
            putExtra("id", alarm.eventID)
            putExtra("notification", "notification")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarm.id.toInt(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Alarm: You have a trip now!")
            .setContentText("It's time! Event name: ${alarm.itineraryName} Event place: ${alarm.itineraryPlace}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }
        notificationManager.notify(alarm.id.toInt(), notificationBuilder.build())
    }

    private fun updateFirebase(context: Context, alarm: ItineraryEntity) {
        val (token, userId) = SharedPreferences().checkToken(context)
        FirebaseManager().udpateItineraryAlaram(userId!!, alarm.eventID, alarm.id.toString(), "ONGOING", false){
            if (it){
                Toast.makeText(context, "Itinerary is ONGOING!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}