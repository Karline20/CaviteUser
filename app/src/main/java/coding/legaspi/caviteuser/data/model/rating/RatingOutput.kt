package coding.legaspi.caviteuser.data.model.rating

data class RatingOutput(
    val date: String,
    val eventid: String,
    val id: String,
    val rate: Float,
    val timestamp: String,
    val userid: String,
    val review: String,
    val name: String
)