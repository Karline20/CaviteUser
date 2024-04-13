package coding.legaspi.caviteuser.presentation.home.rating

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.data.model.rating.Rating
import coding.legaspi.caviteuser.databinding.ActivityRatingBinding
import coding.legaspi.caviteuser.presentation.auth.profilecreation.ProfileCreation
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.home.event.ViewEventActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.DialogHelperImpl
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import com.kaelli.niceratingbar.OnRatingChangedListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


class RatingActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var ratingBinding: ActivityRatingBinding
    lateinit var dialogHelper: DialogHelper
    lateinit var eventid: String
    lateinit var userID: String
    var rate: Float = 0.0f
    lateinit var review: String
    lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ratingBinding = ActivityRatingBinding.inflate(layoutInflater)
        val view = ratingBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        eventid = intent.getStringExtra("eventid").toString()
        userID = intent.getStringExtra("userID").toString()

        setButton()
        setProfile()
        listenToRating()
        getName()
    }

    private fun getName() {
        ratingBinding.progressBar.visibility = VISIBLE
        val responseLiveData = eventViewModel.getByUserId(userID)
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val profile = it.data as ProfileOutput
                    if (profile!=null){
                        ratingBinding.progressBar.visibility = GONE
                        val firstName = profile.firstname
                        val lastName = profile.lastname
                        name = "$firstName $lastName"
                    }
                }
                is Result.Error -> {
                    // Handle error
                    val exception = it.exception
                    // Show error message or handle error state
                    if (exception is IOException) {
                        // Handle network failure
                        Log.e("Check Result", "showNetworkError")
                        ratingBinding.progressBar.visibility = GONE
                        if (exception.equals("java.net.SocketTimeoutException")){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.localizedMessage}"
                                ),
                                positiveButtonFunction = {
                                    recreate()
                                }
                            )
                        } else{
                            // Handle other exceptions
                            ratingBinding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Error",
                                    exception.localizedMessage!!
                                ),
                                positiveButtonFunction = {

                                }
                            )
                            Log.d("Check Result", "Unauthorized")
                        }

                    } else {
                        // Handle other exceptions
                        ratingBinding.progressBar.visibility = GONE
                        dialogHelper.showUnauthorized(
                            Error(
                                "Error",
                                "$exception"
                            ),
                            positiveButtonFunction = {
                                recreate()
                            }
                        )
                        Log.d("Check Result", "showGenericError")
                    }
                }
                Result.Loading -> {
                    // Handle loading state
                    Log.d("Check Result", "Loading")
                    ratingBinding.progressBar.visibility = VISIBLE
                }

            }
        })
    }

    private fun listenToRating() {
        ratingBinding.rating.setOnRatingChangedListener(OnRatingChangedListener {
            if (it<=0) run {
                rate = it
            }else{
                rate = it
            }
        })
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
                        .into(ratingBinding.loggedInTopNav.imgProfile)
                }
            }
        }
    }

    private fun setButton() {
        ratingBinding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        ratingBinding.btnSaveReview.setOnClickListener {
            saveReview()
        }
    }

    private fun saveReview() {
        finishActivity(ViewEventActivity.ViewEventActivity)
        ratingBinding.progressBar.visibility = VISIBLE
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(currentTimeMillis)
        val formattedDate = sdf.format(date)
        if (validate()){
            val responseLiveData = eventViewModel.postRating(Rating(formattedDate, eventid, rate, currentTimeMillis.toString(), userID, review, name))
            responseLiveData.observe(this, Observer {
                when(it) {
                    is Result.Success<*> -> {
                        ratingBinding.progressBar.visibility = GONE
                        dialogHelper.thanksSuccess("Ratings","Thank you for rating us!"){ isTrue ->
                            if (isTrue){
                                val intent = Intent(this, ViewEventActivity::class.java)
                                intent.putExtra("id", eventid)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    is Result.Error -> {
                        val exception = it.exception
                        if (exception is IOException) {
                            ratingBinding.progressBar.visibility = GONE
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
                                dialogHelper.showUnauthorized(
                                    Error(
                                        "Error",
                                        exception.localizedMessage!!
                                    ),
                                    positiveButtonFunction = {

                                    }
                                )
                            }
                        } else {
                            ratingBinding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Error",
                                    "Something went wrong!"
                                ),
                                positiveButtonFunction = {

                                }
                            )
                            Log.d("Check Result", "showGenericError")
                        }
                    }
                    Result.Loading -> {
                        // Handle loading state
                        Log.d("Check Result", "Loading")
                        ratingBinding.progressBar.visibility = VISIBLE
                    }
                }
            })
        }
    }

    private fun validate(): Boolean {
        review = ratingBinding.etReview.text.toString()
        if (review==null){
            ratingBinding.etReview.error = "Empty field!"
        }
        return review!=null
    }
}