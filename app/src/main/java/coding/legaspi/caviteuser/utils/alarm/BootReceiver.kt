package coding.legaspi.caviteuser.utils.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import coding.legaspi.caviteuser.data.local.ItineraryDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Reschedule all alarms from the database
            val alarmDao = ItineraryDatabase.getDatabase(context).itineraryDao()
            // This should be done in a coroutine or another background task
            GlobalScope.launch {
                val alarms = alarmDao.getAllItinerary().value ?: emptyList()
                alarms.forEach { alarm ->
                    AlarmManagerUtil.setAlarm(context, alarm)
                }
            }
        }
    }
}
