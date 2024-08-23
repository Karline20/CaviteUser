package coding.legaspi.caviteuser.data.local

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData

class ItineraryRepository(private val itineraryDao: ItineraryDao) {

    val allItineraries: LiveData<List<ItineraryEntity>> = itineraryDao.getAllItinerary()
    suspend fun insertOrUpdate(application: Application, itinerary: ItineraryEntity): ItineraryEntity? {
        val existingItinerary = itineraryDao.getItineraryByEventId(itinerary.eventID)
        return if (existingItinerary == null){
            val id = itineraryDao.insertOrUpdateItinerary(itinerary)
            return id?.toInt()?.let { itineraryDao.getItineraryById(it) }
        }else{
            Toast.makeText(application, "You have existing upcoming trip for the event!", Toast.LENGTH_SHORT).show()
            existingItinerary
        }
    }
    suspend fun delete(id: Long) {
        itineraryDao.deleteUser(id)
    }

    suspend fun updateItinerary(id: Long, eventID: String, scheduleDateTimestamp: Long, timestamp: String, itineraryName: String, itineraryPlace: String) {
        itineraryDao.updateItinerary(id, eventID, scheduleDateTimestamp, timestamp, itineraryName, itineraryPlace)
    }

    suspend fun getItineraryByEventId(eventID: String): ItineraryEntity?{
        return itineraryDao.getItineraryByEventId(eventID)
    }

}