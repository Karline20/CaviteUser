package coding.legaspi.caviteuser.data.model.addevents

import com.google.gson.annotations.SerializedName

data class AddEventList(
    @SerializedName("results")
    val addEvent: List<AddEvent>
)