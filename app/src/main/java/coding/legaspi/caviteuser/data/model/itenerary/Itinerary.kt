package coding.legaspi.caviteuser.data.model.itenerary

import coding.legaspi.caviteuser.utils.DateUtils

data class Itinerary(
    val id: String,
    val eventID: String,
    val userID: String,
    val itineraryID: String,
    val scheduleDateTimestamp: String,
    val timestamp: String,
    val tripStatus: String,
    val isTripCompleted: Boolean,
    val dateCompleted: String,
    val itineraryImg: String,
    val itineraryPlace: String,
    val itineraryName: String,
    val itineraryFrom: String,
){
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        true,
        "",
        "",
        "",
        "",
        ""
    )
}
