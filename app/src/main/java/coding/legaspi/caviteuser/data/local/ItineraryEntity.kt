package coding.legaspi.caviteuser.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itineraries")
data class ItineraryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val eventID: String,
    val scheduleDateTimestamp: Long,
    val timestamp: String,
    val itineraryName: String,
    val itineraryPlace: String,
    val itineraryImg: String
)
