package coding.legaspi.caviteuser.presentation.home.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.adaptermodel.Image
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput
import coding.legaspi.caviteuser.data.model.favorites.Favorites
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.data.model.rating.Existence
import coding.legaspi.caviteuser.data.model.rating.RatingOutput
import coding.legaspi.caviteuser.databinding.ActivityViewEventBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.home.adapter.EventAdapter
import coding.legaspi.caviteuser.presentation.home.adapter.RatingAdapter
import coding.legaspi.caviteuser.presentation.home.map.MapActivity
import coding.legaspi.caviteuser.presentation.home.rating.RatingActivity
import coding.legaspi.caviteuser.presentation.terms.TermsActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.ImageAdapter
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class ViewEventActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var ratingList: ArrayList<RatingOutput>
    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var imageList: ArrayList<Image>
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivityViewEventBinding
    private lateinit var dialogHelper: DialogHelper
    lateinit var id: String
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var userid: String
    lateinit var location: String
    var isFavExist = false
    companion object{
        val ViewEventActivity = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        id = intent.getStringExtra("id").toString()
        ratingList = arrayListOf()
        ratingAdapter = RatingAdapter(ratingList, this)
        imageList = arrayListOf()
        imageAdapter = ImageAdapter(imageList, this)

        binding.progressBar.visibility = View.GONE
        setData()
        setProfile()
        setButton()
        setRv()
        setImage()
        setFavorite()
    }

    private fun setFavorite() {
        val (token, userId) = SharedPreferences().checkToken(this)
        val checkExistence = eventViewModel.checkExistenceFavorites(Existence(id, userId.toString()))
        checkExistence.observe(this, Observer {
            if (it!=null){
                isFavExist = if (it.body()?.equals(true)!!) {
                    binding.favorites.setImageResource(R.drawable.baseline_favorite_24)
                    true
                }else{
                    binding.favorites.setImageResource(R.drawable.baseline_favorite_border_24)
                    false
                }
                Log.d("FAVORITE", it.body().toString())
            }
        })
    }

    private fun setImage() {
        imageList = arrayListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("Images")
        databaseReference.child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        imageList.clear()
                        for (snapshots in snapshot.children) {
                            val image = snapshots.getValue(Image::class.java)
                            if (image != null) {
                                imageList.add(image)
                            }
                        }
                        imageAdapter = ImageAdapter(imageList, this@ViewEventActivity)
                        binding.epoxyImage.adapter = imageAdapter
                    }
                    else {
                        Log.d("error", "error")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("EventAdapter", "rv Image $error")
                }
            })
    }

    private fun setRv() {
        val responseLiveData = eventViewModel.getRatingByEventId(id)
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as List<RatingOutput>
                    if (result!=null){
                        if (result.isEmpty()){
                            binding.noData.visibility=VISIBLE
                            binding.rvRating.visibility=GONE
                        }else{
                            ratingList.clear()
                            ratingList.addAll(result)
                            val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                            binding.rvRating.layoutManager = llm
                            binding.rvRating.adapter = ratingAdapter
                            binding.progressBar.visibility = GONE
                            ratingAdapter.notifyDataSetChanged()
                        }
                    }else{

                    }
                }
                is Result.Error -> {
                    val exception = it.exception
                    if (exception is IOException) {
                        binding.progressBar.visibility = GONE
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                )
                            )
                        } else{
                            dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!))
                        }
                    } else {
                        binding.progressBar.visibility = GONE
                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"))
                    }
                }
                Result.Loading -> {
                    binding.progressBar.visibility = VISIBLE
                }
            }
        })
    }

    private fun setButton() {
        val (token, userId) = SharedPreferences().checkToken(this)

        binding.rate.setOnClickListener {
            val intent = Intent(this, RatingActivity::class.java)
            intent.putExtra("eventid", id)
            intent.putExtra("userID", userId)
            startActivity(intent)
        }

        binding.viewMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("location", location)
            startActivity(intent)
        }

        binding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.favorites.setOnClickListener {
            binding.progressBar.visibility = VISIBLE
            if (userId != null) {
                if(isFavExist){
                    dialogHelper.already("Favorite exist!", "You have already added to favorites")
                    binding.progressBar.visibility = GONE
                }else{
                    addToFave(userId)
                }
            }
        }
    }

    private fun addToFave(userId: String) {
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(currentTimeMillis)
        val formattedDate = sdf.format(date)
        val responseLiveData = eventViewModel.postFavorites(Favorites(formattedDate, id, currentTimeMillis.toString(), userId))
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    dialogHelper.showSuccess("Favorites", "Added to favorites!")
                    binding.progressBar.visibility = GONE
                }
                is Result.Error -> {
                    val exception = it.exception
                    if (exception is IOException) {
                        Log.e("Check Result", "${exception.localizedMessage}")
                        binding.progressBar.visibility = GONE
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                )
                            )
                        } else{
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Error",
                                    exception.localizedMessage!!
                                )
                            )
                            Log.d("Check Result", "Unauthorized")
                        }
                    } else {
                        binding.progressBar.visibility = GONE
                        dialogHelper.showUnauthorized(
                            Error(
                                "Error",
                                "Something went wrong!"
                            )
                        )
                        Log.d("Check Result", "showGenericError")
                    }
                }
                Result.Loading -> {
                    Log.d("Check Result", "Loading")
                    binding.progressBar.visibility = VISIBLE
                }
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
                        .into(binding.loggedInTopNav.imgProfile)
                }
            }
        }
    }

    private fun setData() {
        val responseLiveData = eventViewModel.getEventsById(id)
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as AllModelOutput
                    binding.labelEventCategory.text = result.eventcategory
                    binding.labelName.text = result.name
                    binding.labelAddress.text = result.location
                    binding.labelDescription.text = result.description
                    binding.labelDate.text = result.date
                    if (result.category!= null){
                        binding.labelCategory.text = result.category
                    }else{
                        binding.labelCategory.visibility = GONE
                    }
                    latitude = result.latitude
                    longitude = result.longitude
                    location = result.location

                }
                is Result.Error -> {
                    val exception = it.exception
                    if (exception is IOException) {
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                )
                            )
                        } else{
                            dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!))
                        }
                    } else {
                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"))
                    }
                }
                Result.Loading -> {

                }
            }
        })
    }
}