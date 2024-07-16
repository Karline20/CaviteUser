package coding.legaspi.caviteuser.presentation.tutorial.tutor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.tutorial.TutorialStatus
import coding.legaspi.caviteuser.databinding.ActivityTutorBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.tutorial.TutorialActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.speechrecognition.SpeechModel
import coding.legaspi.caviteuser.speechrecognition.SpeechRecognizerViewModel
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.MediaPlayerFactory
import coding.legaspi.caviteuser.utils.MediaPlayerHelper
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.tutorial.TutorialStatusOutput
import java.io.IOException

class TutorActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var dialogHelper: DialogHelper
    private lateinit var tutorBinding: ActivityTutorBinding
    private lateinit var mediaPlayerHelper: MediaPlayerHelper
    lateinit var tutorialid: String
    lateinit var userid: String
    lateinit var tutorial: String
    lateinit var tutorialExactPos: String

    private val REQUEST_CODE_SPEECH_INPUT = 1
    private val permission = arrayOf(Manifest.permission.RECORD_AUDIO)
    private lateinit var speechRecognizerViewModel: SpeechRecognizerViewModel

    val TAG = "TalkingTom"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tutorBinding = ActivityTutorBinding.inflate(layoutInflater)
        val view = tutorBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        mediaPlayerHelper = MediaPlayerFactory.create(this)
        tutorialid = intent.getStringExtra("tutorialid").toString()
        userid = intent.getStringExtra("userid").toString()
        tutorial = intent.getStringExtra("tutorial").toString()
        tutorialExactPos = intent.getStringExtra("tutorialExactPos").toString()
        tutorBinding.progressMusic.visibility = GONE
        tutorBinding.llCongrats.visibility = GONE
        tutorBinding.loggedInTopNav.labelTitle.text = tutorial
        tutorBinding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }
        setupSpeechViewModel()
        dialogFun()
        setButton()
        setProfile()
    }

    private fun setupSpeechViewModel() {
        speechRecognizerViewModel = ViewModelProvider(this).get(SpeechRecognizerViewModel::class.java)
        speechRecognizerViewModel.getViewState().observe(this, Observer<SpeechRecognizerViewModel.ViewState> {viewState ->
            render(viewState)
        })
    }

    private fun render(output: SpeechRecognizerViewModel.ViewState?) {
        if (output==null) return
        Log.d(TAG, output.spokenText)
        if (output.isListening) {
            tutorBinding.wave.visibility = VISIBLE
        }else{
            tutorBinding.wave.visibility = GONE
            if(output.spokenText == ""){
                Log.e(TAG, output.spokenText)
            }else{
                SpeechModel().checkModel(output.spokenText, tutorial){
                    if (it){
                        output.spokenText = ""
                        tutorBinding.llCongrats.visibility = VISIBLE
                        dialogHelper.showSuccess("Congrats", "You passed the $tutorial")
                    }else{
                        output.spokenText = ""
                        dialogHelper.showError(Error("Please try again...","Wrong word!"))
                    }
                }
            }
        }
        if (output.rmsDbChanged){
            tutorBinding.wave.visibility = VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT){
            speechRecognizerViewModel.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun setButton() {
        tutorBinding.repeat.setOnClickListener{
            mediaPlayerHelper.playMusic(tutorialExactPos){
                tutorBinding.progressMusic.visibility = GONE
            }
            tutorBinding.progressMusic.visibility = VISIBLE
        }

        tutorBinding.mic.setOnClickListener {
            setMic()
        }

        tutorBinding.next.setOnClickListener {
            dialogHelper.thanksSuccess("Passed", "You passed the $tutorial"){
                if (it){
                    tutorBinding.progressBar.visibility= VISIBLE
                    saveTutorial()
                }
            }
        }
    }

    private fun saveTutorial() {
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(currentTimeMillis)
        val formattedDate = sdf.format(date)

        val responseLiveData = eventViewModel.postTutorialStatus(TutorialStatus(formattedDate, true, currentTimeMillis.toString(), tutorial, tutorialid, userid))
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as TutorialStatusOutput
                    tutorBinding.progressBar.visibility= GONE
                    val intent = Intent(this, TutorialActivity::class.java)
                    startActivity(intent)
                    finishActivity(TutorialActivity.TutorialActivity)
                    finish()
                }
                is Result.Error -> {
                    val exception = it.exception

                    if (exception is IOException) {
                        tutorBinding.progressBar.visibility = GONE
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                ),
                                positiveButtonFunction = {
                                    recreate()
                                }
                            )
                        } else{
                            dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!)
                                ,
                                positiveButtonFunction = {

                                })
                        }
                    } else {
                        tutorBinding.progressBar.visibility = GONE
                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                            positiveButtonFunction = {
                            })
                    }
                }
                Result.Loading -> {
                    tutorBinding.progressBar.visibility = VISIBLE
                }
            }
        })
    }

    private fun setMic() {

        if (!speechRecognizerViewModel.permissionToRecordAudio){
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE_SPEECH_INPUT)
            return
        }
        if (speechRecognizerViewModel.isListening){
            speechRecognizerViewModel.stopListening()
        }else{
            speechRecognizerViewModel.startListening()
        }
    }

    private fun dialogFun() {
        lateinit var value: String
        when(tutorial) {
            "MAGANDA KA - BONITA USTE" -> value = "MAGANDA KA - BO-NI-TA US-TE"
            "MAHAL KITA - TA AMA YO CONTIGO" -> value = "MAHAL KITA - TA A-MA YO CON-TI-GO"
            "AYOS LANG - MUY BIEN" -> value = "AYOS LANG - MUY BI-EN"
            "MAGANDANG HAPON - BUENAS TARDES" -> value = "MAGANDANG HAPON - BUE-NAS TAR-DES"
            "KAMUSTA KA - QUETAL MAN USTE" -> value = "KAMUSTA KA - QUE-TAL MAN US-TE"
            "SALAMAT - GRACIAS" -> value = "SALAMAT - GRA-CIAS"
            "PAALAM - ADIOS" -> value = "PAALAM - AD-IOS"
            "PASINTABI - CON PERMISO"-> value = "PASINTABI - CON PER-MI-SO"
            "TULOY KAYO - BIENVENIDOS"-> value = "TULOY KAYO - BI-EN-VE-NI-DOS"
            "MAGANDANG GABI - BUENAS NOCHES"-> value = "MAGANDANG GABI - BUE-NAS NO-CHES"
            "PATAWAD - PERDONA CONMIGO"-> value = "PATAWAD - PER-DO-NA CON-MI-GO"
            "MAGANDANG UMAGA - BUENAS DIAS"-> value = "MAGANDANG UMAGA - BUE-NAS DI-AS"
            "INGAT KA - QUIDAO"-> value = "INGAT KA - QUI-DAO"
            "AMA - PADRE"-> value = "AMA - PAD-RE"
            "ANAK NA BABAE - HIJA"-> value = "ANAK NA BABAE - HI-JA"
            "ANAK NA LALAKI - HIJO"-> value = "ANAK NA LALAKI - HI-JO"
            "ANONG PANGALAN MO - COSA TU NOMBRE"-> value = "ANONG PANGALAN MO - CO-SA TU NOM-BRE"
            "APO NA LALAKI - NIETO"-> value = "APO NA LALAKI - NI-E-TO"
            "ASAWANG BABAE - MUJER"-> value = "ASAWANG BABAE - MU-JER"
            "ASAWANG LALAKI - MARIDO"-> value = "ASAWANG LALAKI - MA-RI-DO"
            "BABAE NA APO - NIETA"-> value = "BABAE NA APO - NI-E-TA"
            "BAYAW - CUÑADO"-> value = "BAYAW - CU-ÑA-DO"
            "BYENAN NA BABAE - SUEGRA"-> value =  "BYENAN NA BABAE - SU-E-GRA"
            "BYENAN NA LALAKE - SUEGRO"-> value = "BYENAN NA LALAKE - SU-E-GRO"
            "HIPAG - CUÑADA"-> value =  "HIPAG - CU-ÑA-DA"
            "INA - MADRE"-> value = "INA - MAD-RE"
            "KAPATID NA BABAE - HERMANA"-> value = "KAPATID NA BABAE - HER-MA-NA"
            "KAPATID NA LALAKI - HERMANO"-> value = "KAPATID NA LALAKI - HER-MA-NO"
            "LOLA - ABUELA"-> value = "LOLA - ABU-E-LA"
            "LOLO - ABUELO" -> value = "LOLO - ABU-E-LO"
            "MGA KAMAGANAK - PARIENTES"-> value = "MGA KAMAGANAK - PAR-I-EN-TES"
            "SAAN KA NAGMULA - DE DONDE TU"-> value = "SAAN KA NAGMULA - DE DON-DE TU"
            "TITA-TIA"-> value =  "TITA-TI-A"
            "TITO-TIO"-> value = "TITO-TI-O"
        }
        dialogHelper.tutorial(tutorialExactPos, value, "Play", "Cancel"){
            if (it){
                mediaPlayerHelper.playMusic(tutorialExactPos){
                    tutorBinding.progressMusic.visibility = GONE
                }
                tutorBinding.progressMusic.visibility = VISIBLE
                Log.d("Media", it.toString())
            }else{
                Log.d("Media", it.toString())
                dialogHelper.thanksSuccess("Thanks", "Please comeback for this tutorial."){
                    if (it){
                        mediaPlayerHelper.stopMusic()
                        val intent = Intent(this, TutorialActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun setProfile() {
        val (token, userId) = SharedPreferences().checkToken(this)
        if (userId != null){
            FirebaseManager().fetchProfileFromFirebase(userId){
                if (it!=null){
                    Glide.with(this)
                        .load(it.imageUri)
                        .placeholder(R.drawable.baseline_broken_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(tutorBinding.loggedInTopNav.imgProfile)
                }
            }
        }
    }
}