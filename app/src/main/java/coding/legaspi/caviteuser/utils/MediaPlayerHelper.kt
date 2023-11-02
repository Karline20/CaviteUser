package coding.legaspi.caviteuser.utils

interface MediaPlayerHelper {

    fun playMusic(tutorialExactPos: String, callback: (Boolean) -> Unit)

    fun stopMusic()

}