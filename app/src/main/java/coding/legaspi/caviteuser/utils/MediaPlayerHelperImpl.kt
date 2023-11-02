package coding.legaspi.caviteuser.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import coding.legaspi.caviteuser.R
import kotlinx.coroutines.flow.callbackFlow

class MediaPlayerHelperImpl(private val context: Context): MediaPlayerHelper {


    private var mediaPlayer: MediaPlayer? = null

    override fun playMusic(tutorialExactPos: String, callback: (Boolean) -> Unit) {

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        try {
            val resourceUri: Uri = when (tutorialExactPos) {
                "Tutorial 1" -> Uri.parse("android.resource://${context.packageName}/${R.raw.cunada}")
                "Tutorial 2" -> Uri.parse("android.resource://${context.packageName}/${R.raw.abuelo}")
                "Tutorial 3" -> Uri.parse("android.resource://${context.packageName}/${R.raw.magandaka}")
                "Tutorial 4" -> Uri.parse("android.resource://${context.packageName}/${R.raw.mahalkita}")
                "Tutorial 5" -> Uri.parse("android.resource://${context.packageName}/${R.raw.nieto}")
                "Tutorial 6"->Uri.parse("android.resource://${context.packageName}/${R.raw.hermano}")
                "Tutorial 7"->Uri.parse("android.resource://${context.packageName}/${R.raw.mujer}")
                "Tutorial 8"->Uri.parse("android.resource://${context.packageName}/${R.raw.ayuslang}")
                "Tutorial 9"-> Uri.parse("android.resource://${context.packageName}/${R.raw.hijo}")
                "Tutorial 10"->Uri.parse("android.resource://${context.packageName}/${R.raw.madre}")
                "Tutorial 11"-> Uri.parse("android.resource://${context.packageName}/${R.raw.hermana}")
                "Tutorial 12"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tio}")
                "Tutorial 13"-> Uri.parse("android.resource://${context.packageName}/${R.raw.suegra}")
                "Tutorial 14"-> Uri.parse("android.resource://${context.packageName}/${R.raw.marido}")
                "Tutorial 15"-> Uri.parse("android.resource://${context.packageName}/${R.raw.magandanghapon}")
                "Tutorial 16"-> Uri.parse("android.resource://${context.packageName}/${R.raw.parientes}")
                "Tutorial 17"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tia}")
                "Tutorial 18"-> Uri.parse("android.resource://${context.packageName}/${R.raw.cosatunombre}")
                "Tutorial 19"-> Uri.parse("android.resource://${context.packageName}/${R.raw.cunao}")
                "Tutorial 20"-> Uri.parse("android.resource://${context.packageName}/${R.raw.kumustaka}")
                "Tutorial 21"-> Uri.parse("android.resource://${context.packageName}/${R.raw.abuela}")
                "Tutorial 22"-> Uri.parse("android.resource://${context.packageName}/${R.raw.salamat}")
                "Tutorial 23"-> Uri.parse("android.resource://${context.packageName}/${R.raw.paalam}")
                "Tutorial 24"-> Uri.parse("android.resource://${context.packageName}/${R.raw.pasintabi}")
                "Tutorial 25"-> Uri.parse("android.resource://${context.packageName}/${R.raw.padre}")
                "Tutorial 26"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tuloykayo}")
                "Tutorial 27"-> Uri.parse("android.resource://${context.packageName}/${R.raw.magandanggabe}")
                "Tutorial 28"-> Uri.parse("android.resource://${context.packageName}/${R.raw.nieta}")
                "Tutorial 29"-> Uri.parse("android.resource://${context.packageName}/${R.raw.patawad}")
                "Tutorial 30"-> Uri.parse("android.resource://${context.packageName}/${R.raw.dedondetu}")
                "Tutorial 31"-> Uri.parse("android.resource://${context.packageName}/${R.raw.magandangumaga}")
                "Tutorial 32"-> Uri.parse("android.resource://${context.packageName}/${R.raw.suegro}")
                "Tutorial 33"-> Uri.parse("android.resource://${context.packageName}/${R.raw.hija}")
                "Tutorial 34"-> Uri.parse("android.resource://${context.packageName}/${R.raw.ingatka}")
                else -> throw IllegalArgumentException("Invalid tutorialExactPos: $tutorialExactPos")
            }
            mediaPlayer?.setDataSource(context, resourceUri)
            mediaPlayer?.prepare()
            mediaPlayer?.start()

            mediaPlayer?.setOnCompletionListener {
                callback(true)
            }

        } catch (e: Exception) {
            Log.e("MediaPlayerHelperImpl", "$e")
        }
    }

    override fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

}