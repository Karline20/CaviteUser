package coding.legaspi.caviteuser.presentation.home.rating

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.rating.Rating
import coding.legaspi.caviteuser.databinding.ActivityRatingBinding
import coding.legaspi.caviteuser.presentation.di.Injector
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
            if (it!=null){
                ratingBinding.progressBar.visibility = GONE
                val firstName = it.body()?.firstname.toString()
                val lastName = it.body()?.lastname.toString()
                name = "$firstName $lastName"
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
                if (it.isSuccessful){
                    ratingBinding.progressBar.visibility = GONE
                    dialogHelper.thanksSuccess("Ratings","Thank you for rating us!"){ isTrue ->
                        if (isTrue){
                            val intent = Intent(this, ViewEventActivity::class.java)
                            intent.putExtra("id", eventid)
                            startActivity(intent)
                            finish()
                        }
                    }
                }else{
                    dialogHelper.showUnauthorized(Error(it.code().toString(), it.message()))
                    ratingBinding.progressBar.visibility = GONE
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