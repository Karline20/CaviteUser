package coding.legaspi.caviteuser.data.model.tutorial

data class TutorialStatusOutput(
    val date: String,
    val id: String,
    val isFinish: Boolean,
    val timestamp: String,
    val tutorial: String,
    val tutorialid: String,
    val userid: String
)
