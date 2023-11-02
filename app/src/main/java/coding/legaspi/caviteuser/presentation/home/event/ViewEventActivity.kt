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
import coding.legaspi.caviteuser.data.model.adaptermodel.Image
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.favorites.Favorites
import coding.legaspi.caviteuser.data.model.rating.Existence
import coding.legaspi.caviteuser.data.model.rating.RatingOutput
import coding.legaspi.caviteuser.databinding.ActivityViewEventBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.adapter.EventAdapter
import coding.legaspi.caviteuser.presentation.home.adapter.RatingAdapter
import coding.legaspi.caviteuser.presentation.home.map.MapActivity
import coding.legaspi.caviteuser.presentation.home.rating.RatingActivity
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
            if (it!=null){
                if (it.isEmpty()){
                    binding.noData.visibility=VISIBLE
                    binding.rvRating.visibility=GONE
                }else{
                    ratingList.clear()
                    ratingList.addAll(it)
                    val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    binding.rvRating.layoutManager = llm
                    binding.rvRating.adapter = ratingAdapter
                    binding.progressBar.visibility = GONE
                    ratingAdapter.notifyDataSetChanged()
                }
            }else{

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
            if (it!=null){
                dialogHelper.showSuccess("Favorites", "Added to favorites!")
                binding.progressBar.visibility = GONE
            }else{
                dialogHelper.showError(Error("Connection error", "Something went wrong!"))
                binding.progressBar.visibility = GONE
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
            if (it!=null){
                binding.labelEventCategory.text = it.body()?.eventcategory
                binding.labelName.text = it.body()?.name
                binding.labelAddress.text = it.body()?.location
                binding.labelDescription.text = it.body()?.description
                binding.labelDate.text = it.body()?.date
                if (it.body()?.category!= null){
                    binding.labelCategory.text = it.body()?.category
                }else{
                    binding.labelCategory.visibility = GONE
                }
                latitude = it.body()?.latitude.toString()
                longitude = it.body()?.longitude.toString()
                location = it.body()?.location.toString()
            }else{

            }
        })
    }
}