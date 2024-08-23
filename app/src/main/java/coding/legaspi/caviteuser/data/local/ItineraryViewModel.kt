package coding.legaspi.caviteuser.data.local

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ItineraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItineraryRepository
    val allItineraries: LiveData<List<ItineraryEntity>>

    init {
        val itineraryDao = ItineraryDatabase.getDatabase(application).itineraryDao()
        repository = ItineraryRepository(itineraryDao)
        allItineraries = repository.allItineraries
    }
    fun insertOrUpdate(itinerary: ItineraryEntity): LiveData<ItineraryEntity> {
        val result = MutableLiveData<ItineraryEntity>()
        viewModelScope.launch {
            val updatedItinerary  = repository.insertOrUpdate(getApplication(), itinerary)
            result.postValue(updatedItinerary!!)
        }
        return result

    }
    fun delete(id: Long) = viewModelScope.launch {
        repository.delete(id)
    }

    fun updateItinerary(id: Long, eventID: String, scheduleDateTimestamp: Long, timestamp: String, itineraryName: String, itineraryPlace: String) = viewModelScope.launch {
        repository.updateItinerary(id, eventID, scheduleDateTimestamp, timestamp, itineraryName, itineraryPlace)
    }

    fun getItineraryByEventId(eventID: String): LiveData<ItineraryEntity?>{
        val result = MutableLiveData<ItineraryEntity?>()
        viewModelScope.launch {
            val itinerary = repository.getItineraryByEventId(eventID)
            result.postValue(itinerary)
        }
        return result
    }
}