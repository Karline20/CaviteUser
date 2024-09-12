package coding.legaspi.caviteuser.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import coding.legaspi.caviteuser.R

class MediaPlayerHelperImpl(private val context: Context): MediaPlayerHelper {


    private var mediaPlayer: MediaPlayer? = null

    override fun playMusic(tutorialExactPos: String, callback: (Boolean) -> Unit) {

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        try {
            val resourceUri: Uri = when (tutorialExactPos) {
                "Tutorial 1" -> Uri.parse("android.resource://${context.packageName}/${R.raw.padre}")
                "Tutorial 2" -> Uri.parse("android.resource://${context.packageName}/${R.raw.hija}")
                "Tutorial 3" -> Uri.parse("android.resource://${context.packageName}/${R.raw.hijo}")
                "Tutorial 4" -> Uri.parse("android.resource://${context.packageName}/${R.raw.cosatunombre}")
                "Tutorial 5" -> Uri.parse("android.resource://${context.packageName}/${R.raw.nieto}")
                "Tutorial 6"->Uri.parse("android.resource://${context.packageName}/${R.raw.mujer}")
                "Tutorial 7"->Uri.parse("android.resource://${context.packageName}/${R.raw.marido}")
                "Tutorial 8"->Uri.parse("android.resource://${context.packageName}/${R.raw.ayuslang}")
                "Tutorial 9"-> Uri.parse("android.resource://${context.packageName}/${R.raw.nieta}")
                "Tutorial 10"->Uri.parse("android.resource://${context.packageName}/${R.raw.cunado}")
                "Tutorial 11"-> Uri.parse("android.resource://${context.packageName}/${R.raw.byahi}")
                "Tutorial 12"-> Uri.parse("android.resource://${context.packageName}/${R.raw.suegra}")
                "Tutorial 13"-> Uri.parse("android.resource://${context.packageName}/${R.raw.suegro}")
                "Tutorial 14"-> Uri.parse("android.resource://${context.packageName}/${R.raw.aqui}")
                "Tutorial 15"-> Uri.parse("android.resource://${context.packageName}/${R.raw.allya}")
                "Tutorial 16"-> Uri.parse("android.resource://${context.packageName}/${R.raw.cunada}")
                "Tutorial 17"-> Uri.parse("android.resource://${context.packageName}/${R.raw.madre}")
                "Tutorial 18"-> Uri.parse("android.resource://${context.packageName}/${R.raw.ingatka}")
                "Tutorial 19"-> Uri.parse("android.resource://${context.packageName}/${R.raw.kumustaka}")
                "Tutorial 20"-> Uri.parse("android.resource://${context.packageName}/${R.raw.hermana}")
                "Tutorial 21"-> Uri.parse("android.resource://${context.packageName}/${R.raw.hermano}")
                "Tutorial 22"-> Uri.parse("android.resource://${context.packageName}/${R.raw.abuela}")
                "Tutorial 23"-> Uri.parse("android.resource://${context.packageName}/${R.raw.abuelo}")
                "Tutorial 24"-> Uri.parse("android.resource://${context.packageName}/${R.raw.magandaka}")
                "Tutorial 25"-> Uri.parse("android.resource://${context.packageName}/${R.raw.magandanggabe}")
                "Tutorial 26"-> Uri.parse("android.resource://${context.packageName}/${R.raw.magandanghapon}")
                "Tutorial 27"-> Uri.parse("android.resource://${context.packageName}/${R.raw.magandangumaga}")
                "Tutorial 28"-> Uri.parse("android.resource://${context.packageName}/${R.raw.kwantu}")
                "Tutorial 29"-> Uri.parse("android.resource://${context.packageName}/${R.raw.mahalkita}")
                "Tutorial 30"-> Uri.parse("android.resource://${context.packageName}/${R.raw.selca}")
                "Tutorial 31"-> Uri.parse("android.resource://${context.packageName}/${R.raw.lejos}")
                "Tutorial 32"-> Uri.parse("android.resource://${context.packageName}/${R.raw.parientes}")
                "Tutorial 33"-> Uri.parse("android.resource://${context.packageName}/${R.raw.paalam}")
                "Tutorial 34"-> Uri.parse("android.resource://${context.packageName}/${R.raw.dibisita}")
                "Tutorial 35"-> Uri.parse("android.resource://${context.packageName}/${R.raw.dipasya}")
                "Tutorial 36"-> Uri.parse("android.resource://${context.packageName}/${R.raw.pasintabi}")
                "Tutorial 37"-> Uri.parse("android.resource://${context.packageName}/${R.raw.patawad}")
                "Tutorial 38"-> Uri.parse("android.resource://${context.packageName}/${R.raw.ondi}")
                "Tutorial 39"-> Uri.parse("android.resource://${context.packageName}/${R.raw.dedondetu}")
                "Tutorial 40"-> Uri.parse("android.resource://${context.packageName}/${R.raw.dondidinda}")
                "Tutorial 41"-> Uri.parse("android.resource://${context.packageName}/${R.raw.salamat}")
                "Tutorial 42"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tia}")
                "Tutorial 43"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tio}")
                "Tutorial 44"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tuloykayo}")
                "Tagalog Hymn"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tagalog_hymn}")
                "Chabacano Hymn"-> Uri.parse("android.resource://${context.packageName}/${R.raw.chabacano_hymn}")
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