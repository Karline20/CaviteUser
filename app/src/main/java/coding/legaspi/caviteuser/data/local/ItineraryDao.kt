package coding.legaspi.caviteuser.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itineraries")
    fun getAllItinerary(): LiveData<List<ItineraryEntity>>
    @Query("SELECT * FROM itineraries WHERE eventID = :eventID LIMIT 1")
    suspend fun getItineraryByEventId(eventID: String): ItineraryEntity?
    @Insert
    suspend fun insertOrUpdateItinerary(itineraryEntity: ItineraryEntity): Long?
    @Query("DELETE FROM itineraries WHERE id = :id")
    suspend fun deleteUser(id: Long)

    @Query("UPDATE itineraries SET eventID = :eventID, scheduleDateTimestamp = :scheduleDateTimestamp, timestamp = :timestamp, itineraryName = :itineraryName, itineraryPlace = :itineraryPlace WHERE id = :id")
    suspend fun updateItinerary(id: Long, eventID: String, scheduleDateTimestamp: Long, timestamp: String, itineraryName: String, itineraryPlace: String)

    @Query("SELECT * FROM itineraries WHERE id = :id")
    suspend fun getItineraryById(id: Int): ItineraryEntity?

}