package coding.legaspi.caviteuser.presentation.play

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.ranking.PatchRank
import coding.legaspi.caviteuser.data.model.ranking.Ranking
import coding.legaspi.caviteuser.databinding.ActivityPlayBinding
import coding.legaspi.caviteuser.presentation.about.AboutActivity
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.play.ranking.LeaderBoardsActivity
import coding.legaspi.caviteuser.presentation.tutorial.TutorialActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.speechrecognition.SpeechModel
import coding.legaspi.caviteuser.speechrecognition.SpeechRecognizerViewModel
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.data.model.ranking.RankingOutput
import java.io.IOException

class PlayActivity : AppCompatActivity() {

    val TAG = "TalkingTom"
    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var dialogHelper: DialogHelper
    private lateinit var activityPlayBinding: ActivityPlayBinding
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private val permission = arrayOf(Manifest.permission.RECORD_AUDIO)
    private lateinit var speechRecognizerViewModel: SpeechRecognizerViewModel

    var isCountdownRunning = false
    lateinit var tutorial: String
    lateinit var countdownTimer: CountDownTimer
    lateinit var counterToStart: CountDownTimer
    private var correctCount = 0
    private var countSpeechNumber = 0
    private val generatedNumbers = mutableListOf<Int>()
    lateinit var firstname: String
    lateinit var lastname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPlayBinding = ActivityPlayBinding.inflate(layoutInflater)
        val view = activityPlayBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)

        dialogHelper = DialogHelperFactory.create(this)

        activityPlayBinding.loggedInTopNav.rrlFirst.visibility = GONE
        activityPlayBinding.loggedInTopNav.labelTitle.text = "Pronunciation quiz"

        setBottomButton()
        setProfile()
        setPlay()
        setButton()
        getName()
    }

    override fun onResume() {
        super.onResume()
        setBottomButton()
        setProfile()
        setPlay()
        setButton()
        getName()
    }

    private fun getName(){
        val (token, userId) = SharedPreferences().checkToken(this)
        val responseLiveData = eventViewModel.getUserId(userId!!)
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as ProfileOutput
                    if (result!=null){
                        firstname = result.firstname
                        lastname = result.lastname
                    }

                }
                is Result.Error -> {
                    val exception = it.exception
                    if (exception is IOException) {
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
                            dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!),
                                positiveButtonFunction = {

                                })
                        }
                    } else {
                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                            positiveButtonFunction = {
                            })
                    }
                }
                Result.Loading -> {
                }
            }
        })
    }

    private fun setButton() {
        activityPlayBinding.mic.setOnClickListener{
            setMic()
        }
        activityPlayBinding.rrlRanking.setOnClickListener{
            val intent = Intent(this, LeaderBoardsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setPlay() {
        dialogHelper.play(this,
            "Pronunciation quiz",
            "Use headphones for improved voice recognition and clearer voice. \n Do you want to play with us?",
            "Play",
            "Quit",
            positiveButtonFunction = {
                countSpeechNumber = 0
                timerToStart()
            },
            negativeButtonFunction = {
                dialogHelper.showLogout("Quit", "Are you sure you want to quit?", "Yes", "No" ) {
                    if (it) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        setPlay()
                    }
                }
            },
            leaderBoardsButtonFunction = {
                val intent = Intent(this, LeaderBoardsActivity::class.java)
                startActivity(intent)
            }
        )
    }

    private fun timerToStart() {
        counterToStart = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                activityPlayBinding.txtTimerToStart.text = "$secondsRemaining"
            }
            override fun onFinish() {
                activityPlayBinding.txtTimerToStart.text = "Start!"
                activityPlayBinding.timerToStart.visibility = GONE
                generateRandomNumber()
            }
        }
        counterToStart.start()
    }

    private fun generateRandomNumber() {
        if (generatedNumbers.size <= 11) {
            var random: Int
            do {
                random = Random.nextInt(1, 35)
            } while (random in generatedNumbers)
            generatedNumbers.add(random)

            when(random){
                1 -> tutorial = "MAGANDA KA ->"
                2 -> tutorial = "MAHAL KITA ->"
                3 -> tutorial = "AYOS LANG -> MUY BIEN"
                4 -> tutorial = "MAGANDANG HAPON ->"
                5 -> tutorial = "KAMUSTA KA -> QUETAL MAN USTE"
                6 -> tutorial = "SALAMAT -> GRACIAS"
                7 -> tutorial = "PAALAM -> ADIOS"
                8 -> tutorial = "PASINTABI -> CON PERMISO"
                9 -> tutorial = "TULOY KAYO -> BIENVENIDOS"
                10 -> tutorial = "MAGANDANG GABI ->"
                11 -> tutorial = "PATAWAD ->"
                12 -> tutorial = "MAGANDANG UMAGA ->"
                13 -> tutorial = "INGAT KA ->"
                14 -> tutorial = "AMA - PADRE"
                15 -> tutorial = "ANAK NA BABAE - HIJA"
                16 -> tutorial = "ANAK NA LALAKI - HIJO"
                17 -> tutorial = "ANONG PANGALAN MO_ - COSA TU NOMBRE_"
                18 -> tutorial = "APO NA LALAKI - NIETO"
                19 -> tutorial = "ASAWANG BABAE - MUJER"
                20 -> tutorial = "ASAWANG LALAKI - MARIDO"
                21 -> tutorial = "BABAE NA APO - NIETA"
                22 -> tutorial = "BAYAW - CUÑAO"
                23 -> tutorial = "BYENAN NA BABAE - SUEGRA"
                24 -> tutorial = "BYENAN NA LALAKE - SUEGRO"
                25 -> tutorial = "HIPAG - CUÑADA"
                26 -> tutorial = "INA - MADRE"
                27 -> tutorial = "KAPATID NA BABAE - HERMANA"
                28 -> tutorial = "KAPATID NA LALAKI - HERMANO"
                29 -> tutorial = "LOLA - ABUELA"
                30 -> tutorial = "LOLO - ABUELO"
                31 -> tutorial = "MGA KAMAGANAK - PARIENTES"
                32 -> tutorial = "SAAN KA NAGMULA_ - DE DONDE TU_"
                33 -> tutorial = "TITA-TIA"
                34 -> tutorial = "TITO-TIO"
            }
            countSpeechNumber++
            activityPlayBinding.labelSpeech.text = tutorial
            activityPlayBinding.labelQuestion.text = "Speech $countSpeechNumber"
            activityPlayBinding.totalScore.text = "$correctCount/10"
            setupSpeechViewModel()
            if (isCountdownRunning){
                countdownTimer.cancel()
                timer()
            }else{
                timer()
            }
            if (generatedNumbers.size > 10) {
                println("Reached 10 unique numbers. Stopping.")
                dialogHelper.play(this,
                    "Game set!",
                    "Your total score is $correctCount/10",
                    "Okay",
                    "Quit",
                    positiveButtonFunction = {
                        activityPlayBinding.progressBar.visibility = VISIBLE
                        val currentTimeMillis = System.currentTimeMillis()
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = Date(currentTimeMillis)
                        val formattedDate = sdf.format(date)
                        Toast.makeText(this, "Clearing game...", Toast.LENGTH_SHORT).show()
                        val (token, userId) = SharedPreferences().checkToken(this)
                        val checkExistence = eventViewModel.checkRanking(userId.toString())
                        checkExistence.observe(this, Observer {
                            val doesExist = it.body().toString()
                            if (doesExist.equals(true)){
                                val responseLiveData = eventViewModel.patchRank(userId.toString(), PatchRank(formattedDate,correctCount, currentTimeMillis.toString()))
                                responseLiveData.observe(this, Observer {
                                    if (it!=null){
                                        activityPlayBinding.progressBar.visibility = GONE
                                        setPlay()
                                    }
                                })
                            }else{
                                val responseLiveData = eventViewModel.postRank(Ranking(formattedDate, "$firstname, $lastname", correctCount, currentTimeMillis.toString(), userId!!))
                                responseLiveData.observe(this, Observer {
                                    if (it!=null){
                                        activityPlayBinding.progressBar.visibility = GONE
                                        setPlay()
                                    }
                                })
                            }
                        })
                    },
                    negativeButtonFunction = {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    leaderBoardsButtonFunction = {
                        val intent = Intent(this, LeaderBoardsActivity::class.java)
                        startActivity(intent)
                    }
                )
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
                        .into(activityPlayBinding.loggedInTopNav.imgProfile)
                }
            }
        }
    }
    private fun setBottomButton() {
        activityPlayBinding.loggedInBottomNav.play.setImageResource(R.drawable.baseline_quiz_24)

        activityPlayBinding.loggedInBottomNav.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityPlayBinding.loggedInBottomNav.tutorial.setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
            finish()
        }
        activityPlayBinding.loggedInBottomNav.play.setOnClickListener {

        }
        activityPlayBinding.loggedInBottomNav.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }
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
            activityPlayBinding.wave.visibility = VISIBLE
            activityPlayBinding.labelRepeat.text = "Please speak the word!"
        }else{
            activityPlayBinding.wave.visibility = View.GONE
            activityPlayBinding.labelRepeat.text = "Click here if you want to speak the word!"
            if(output.spokenText == ""){
                Log.e(TAG, output.spokenText)
            }else{
                SpeechModel().checkModel(output.spokenText, tutorial){
                    if (it){
                        output.spokenText = ""
                        correctCount++
                        Log.d(TAG, correctCount.toString())
                        dialogHelper.tutorial("Correct!", "Your pronunciation is correct!", "Next", "Quit"){
                            if (it){
                                output.spokenText = ""
                                generateRandomNumber()
                            }else{
                                dialogHelper.showLogout("Quit", "Are you sure you want to quit?", "Yes", "No" ){
                                    if (it){
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        output.spokenText = ""
                                        generateRandomNumber()
                                    }
                                }
                            }
                        }
                    }else{
                        output.spokenText = ""
                        dialogHelper.wrong("Wrong!","Your pronunciation is wrong!", "Next", "Quit"){
                            if (it){
                                output.spokenText = ""
                                generateRandomNumber()
                            }else{
                                dialogHelper.showLogout("Quit", "Are you sure you want to quit?", "Yes", "No" ){
                                    if (it){
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        output.spokenText = ""
                                        generateRandomNumber()
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        if (output.rmsDbChanged){
            activityPlayBinding.wave.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT){
            speechRecognizerViewModel.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun timer(){
        countdownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                activityPlayBinding.timer.text = "Time Remaining: $secondsRemaining seconds"
            }

            override fun onFinish() {
                activityPlayBinding.timer.text = "Time's up!"
                dialogHelper.wrong("Time's up!","Sorry!", "Next", "Quit"){
                    if (it){
                        generateRandomNumber()
                    }else{
                        dialogHelper.showLogout("Quit", "Are you sure you want to quit?", "Yes", "No" ){
                            if (it){
                                val intent = Intent(this@PlayActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                generateRandomNumber()
                            }
                        }
                    }
                }
            }
        }
        isCountdownRunning = true
        countdownTimer.start()
    }
}