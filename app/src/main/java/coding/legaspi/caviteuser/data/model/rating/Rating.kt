package coding.legaspi.caviteuser.data.model.rating

data class Rating(
    val date: String,
    val eventid: String,
    val rate: Float,
    val timestamp: String,
    val userid: String,
    val review: String,
    val name: String
)