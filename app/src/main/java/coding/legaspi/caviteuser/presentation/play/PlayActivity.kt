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
    lateinit var gapTimer: CountDownTimer
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
        //setPlay()
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
                if (it){
                    //countSpeechNumber = 0
                    timerToStart()
                }
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
                activityPlayBinding.timerToStart.visibility = VISIBLE
                activityPlayBinding.txtTimerToStart.text = "$secondsRemaining"
            }
            override fun onFinish() {
                activityPlayBinding.txtTimerToStart.text = "Start!"
                activityPlayBinding.timerToStart.visibility = GONE
                generateRandomNumber(true)
            }
        }
        counterToStart.start()
    }

    private fun generateRandomNumber(isFirstTime: Boolean) {
        if (isFirstTime){
            generateNumbersSize()
        }else{
            gapTimer = object  : CountDownTimer(5000, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    activityPlayBinding.timerToStart.visibility = VISIBLE
                    activityPlayBinding.txtTimerToStart.text = "$secondsRemaining"
                    countdownTimer.cancel()
                }
                override fun onFinish() {
                    generateNumbersSize()
                }
            }
            gapTimer.start()
        }
    }

    private fun generateNumbersSize(){
        activityPlayBinding.timerToStart.visibility = GONE
        if (generatedNumbers.size <= 11) {
            var random: Int
            do {
                random = Random.nextInt(1, 44)
            } while (random in generatedNumbers)
            generatedNumbers.add(random)

            when(random){
                1 -> tutorial = "MAGANDA KA - BO-NI-TA US-TE"
                2 -> tutorial =  "MAHAL KITA - TA A-MA YO CON-TI-GO"
                3 -> tutorial = "AYOS LANG - MUY BI-EN"
                4 -> tutorial ="MAGANDANG HAPON - BUE-NAS TAR-DES"
                5 -> tutorial = "KAMUSTA KA - QUE-TAL MAN US-TE"
                6 -> tutorial =  "SALAMAT - GRA-CIAS"
                7 -> tutorial = "PAALAM - AD-IOS"
                8 -> tutorial = "PASINTABI - CON PER-MI-SO"
                9 -> tutorial = "TULOY KAYO - BI-EN-VE-NI-DOS"
                10 -> tutorial = "MAGANDANG GABI - BUE-NAS NO-CHES"
                11 -> tutorial = "PATAWAD - PER-DO-NA CON-MI-GO"
                12 -> tutorial = "MAGANDANG UMAGA - BUE-NAS DI-AS"
                13 -> tutorial = "INGAT KA - QUI-DAO"
                14 -> tutorial ="AMA - PAD-RE"
                15 -> tutorial = "ANAK NA BABAE - HI-JA"
                16 -> tutorial ="ANAK NA LALAKI - HI-JO"
                17 -> tutorial = "ANONG PANGALAN MO - CO-SA TU NOM-BRE"
                18 -> tutorial = "APO NA LALAKI - NI-E-TO"
                19 -> tutorial = "ASAWANG BABAE - MU-JER"
                20 -> tutorial = "ASAWANG LALAKI - MA-RI-DO"
                21 -> tutorial = "BABAE NA APO - NI-E-TA"
                22 -> tutorial = "BAYAW - CU-ÑA-DO"
                23 -> tutorial = "BYENAN NA BABAE - SU-E-GRA"
                24 -> tutorial = "BYENAN NA LALAKE - SU-E-GRO"
                25 -> tutorial =  "HIPAG - CU-ÑA-DA"
                26 -> tutorial = "INA - MAD-RE"
                27 -> tutorial =  "KAPATID NA BABAE - HER-MA-NA"
                28 -> tutorial = "KAPATID NA LALAKI - HER-MA-NO"
                29 -> tutorial =  "LOLA - ABU-E-LA"
                30 -> tutorial = "LOLO - ABU-E-LO"
                31 -> tutorial =  "MGA KAMAGANAK - PAR-I-EN-TES"
                32 -> tutorial = "SAAN KA NAGMULA - DE DON-DE TU"
                33 -> tutorial = "TITA-TI-A"
                34 -> tutorial = "TITO-TI-O"
                35 -> tutorial = "PAG BISITA - DI-BI-SI-TA"
                36 -> tutorial = "PAMAMASYAL - DI-PAS-YA"
                37 -> tutorial = "SAAN PUPUNTA - DON-DI DIN-DA"
                38 -> tutorial = "BYAHE - BYA-HI"
                39 -> tutorial = "DITO - A-QUI"
                40 -> tutorial = "DOON - ALL-YA"
                41 -> tutorial = "MAGKANO - KWAN-TU"
                42 -> tutorial = "MALAPIT - SEL-CA"
                43 -> tutorial = "MALAYO - LE-JOS"
                44 -> tutorial = "SAAN - ON-DI"
            }

            countSpeechNumber++
            runOnUiThread {
                activityPlayBinding.labelSpeech.text = tutorial
            }
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
                dialogHelper.play(this@PlayActivity,
                    "Game set!",
                    "Your total score is $correctCount/10",
                    "Okay",
                    "Quit",
                    positiveButtonFunction = {
                        if (it){
                            activityPlayBinding.progressBar.visibility = VISIBLE
                            val currentTimeMillis = System.currentTimeMillis()
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val date = Date(currentTimeMillis)
                            val formattedDate = sdf.format(date)
                            Toast.makeText(this@PlayActivity, "Clearing game...", Toast.LENGTH_SHORT).show()
                            val (token, userId) = SharedPreferences().checkToken(this@PlayActivity)
                            val checkExistence = eventViewModel.checkRanking(userId.toString())
                            checkExistence.observe(this@PlayActivity, Observer {
                                val doesExist = it.body().toString()
                                if (doesExist.equals(true)){
                                    val responseLiveData = eventViewModel.patchRank(userId.toString(), PatchRank(formattedDate,correctCount, currentTimeMillis.toString()))
                                    responseLiveData.observe(this@PlayActivity, Observer {
                                        if (it!=null){
                                            // Clear game state variables
                                            countSpeechNumber = 0
                                            tutorial = ""
                                            correctCount = 0
                                            generatedNumbers.clear()
                                            // Clear the UI elements
                                            activityPlayBinding.labelSpeech.text = tutorial
                                            activityPlayBinding.labelQuestion.text = "Speech $countSpeechNumber"
                                            activityPlayBinding.totalScore.text = "$correctCount/10"
                                            activityPlayBinding.progressBar.visibility = GONE
                                            setPlay()
                                        }
                                    })
                                }else{
                                    val responseLiveData = eventViewModel.postRank(Ranking(formattedDate, "$firstname, $lastname", correctCount, currentTimeMillis.toString(), userId!!))
                                    responseLiveData.observe(this@PlayActivity, Observer {
                                        if (it!=null){
                                            // Clear game state variables
                                            countSpeechNumber = 0
                                            tutorial = ""
                                            correctCount = 0
                                            generatedNumbers.clear()
                                            // Clear the UI elements
                                            activityPlayBinding.labelSpeech.text = tutorial
                                            activityPlayBinding.labelQuestion.text = "Speech $countSpeechNumber"
                                            activityPlayBinding.totalScore.text = "$correctCount/10"
                                            activityPlayBinding.progressBar.visibility = GONE
                                            setPlay()
                                        }
                                    })
                                }
                            })
                        }
                    },
                    negativeButtonFunction = {
                        val intent = Intent(this@PlayActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    leaderBoardsButtonFunction = {
                        val intent = Intent(this@PlayActivity, LeaderBoardsActivity::class.java)
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
                    Log.d(TAG, "RESULT: "+output.spokenText)
                    if (it){
                        Log.d(TAG, "CORRECT RESULT: "+output.spokenText)
                        output.spokenText = ""
                        correctCount++
                        Log.d(TAG, correctCount.toString())
                        dialogHelper.tutorial("Correct!", "Your pronunciation is correct!", "Next", "Quit"){
                            if (it){
                                output.spokenText = ""
                                generateRandomNumber(false)
                            }else{
                                dialogHelper.showLogout("Quit", "Are you sure you want to quit?", "Yes", "No" ){
                                    if (it){
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        output.spokenText = ""
                                        generateRandomNumber(false)
                                    }
                                }
                            }
                        }
                    }else {
                        Log.d(TAG, "WRONG RESULT: " + output.spokenText)
                        output.spokenText = ""
                        if (!isFinishing && !isDestroyed) {
                            dialogHelper.wrong(
                                "Wrong!",
                                "Your pronunciation is wrong!",
                                "Next",
                                "Quit"
                            ) {
                                if (it) {
                                    output.spokenText = ""
                                    generateRandomNumber(false)
                                } else {
                                    dialogHelper.showLogout(
                                        "Quit",
                                        "Are you sure you want to quit?",
                                        "Yes",
                                        "No"
                                    ) {
                                        if (it) {
                                            val intent = Intent(this, HomeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            output.spokenText = ""
                                            generateRandomNumber(false)
                                        }
                                    }

                                }
                            }
                        }else{
                            Log.e("PlayActivity", "Activity is not valid. Dialog cannot be shown.")

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
                if (!isFinishing && !isDestroyed) {
                dialogHelper.wrong("Time's up!","Sorry!", "Next", "Quit") {
                    if (it) {
                        countdownTimer.cancel()
                        generateRandomNumber(false)
                    } else {
                        dialogHelper.showLogout(
                            "Quit",
                            "Are you sure you want to quit?",
                            "Yes",
                            "No"
                        ) {
                            if (it) {
                                countdownTimer.cancel()
                                val intent = Intent(this@PlayActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                countdownTimer.cancel()
                                generateRandomNumber(false)
                            }
                        }
                    }
                }
                }else {
                        Log.e("PlayActivity", "Activity is not valid. Dialog cannot be shown.")
                    }
            }
        }
        isCountdownRunning = true
        countdownTimer.start()
    }
}