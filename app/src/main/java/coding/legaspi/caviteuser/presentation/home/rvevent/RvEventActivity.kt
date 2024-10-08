package coding.legaspi.caviteuser.presentation.home.rvevent

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput
import coding.legaspi.caviteuser.databinding.ActivityRvEventBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.adapter.EventAdapter
import coding.legaspi.caviteuser.presentation.menu.MenuActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.MediaPlayerFactory
import coding.legaspi.caviteuser.utils.MediaPlayerHelper
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import java.io.IOException
import javax.inject.Inject


class RvEventActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityRvEventBinding

    lateinit var name : String
    private lateinit var eventAdapter: EventAdapter
    private lateinit var allModelOutput: ArrayList<AllModelOutput>

    private lateinit var dialogHelper: DialogHelper
    private lateinit var mediaPlayerHelper: MediaPlayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRvEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        name = intent.getStringExtra("name").toString()
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        mediaPlayerHelper = MediaPlayerFactory.create(this)
        allModelOutput = arrayListOf()
        eventAdapter = EventAdapter(allModelOutput, this)
        dialogHelper = DialogHelperFactory.create(this)
        binding.loggedInTopNav.rrlFirst.visibility = GONE
        binding.loggedInTopNav.labelTitle.text = "$name"

        setProfile()
        setMenu()
        listenToSearch()
    }

    private fun setProfile() {
        val (token, userId) = SharedPreferences().checkToken(this)
        if (userId != null){
            Log.d("HomeActivity", "userId: $userId")
            FirebaseManager().fetchProfileFromFirebase(userId){
                if (it!=null){
                    Glide.with(this)
                        .load(it.imageUri)
                        .placeholder(R.drawable.baseline_broken_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(binding.loggedInTopNav.imgProfile)
                    Log.d("HomeActivity", "if")
                }else{
                    Log.d("HomeActivity", "else")
                }
            }
        }
    }

    private fun setCategory(name: String) {
        binding.llHistory.visibility = GONE
        binding.llContacts.visibility = GONE
        binding.llHotres.visibility = GONE
        binding.llFoods.visibility = GONE
        binding.llSchool.visibility = GONE
        binding.hymnBar.layoutHymn.visibility = GONE
        when(name){
            "History" -> {
                binding.llHistory.visibility = VISIBLE
                setBindingHistory()
            }
            "Foods" -> {
                binding.llFoods.visibility = VISIBLE
                setBindingFoods()
            }
            "Hotel & Resorts" -> {
                binding.llHotres.visibility = VISIBLE
                setBindingHotel()
            }
            "Emergency Care & Contacts" -> {
                binding.llContacts.visibility = VISIBLE
                setBindingContacts()
            }
            "Schools" -> {
                binding.llSchool.visibility = VISIBLE
                setBindingSchool()
            }
            "Cavite City Hymn" -> {
                binding.noData.visibility = GONE
                binding.rrlFirst.visibility = GONE
                binding.rrMid.visibility = GONE
                binding.rrlSecond.visibility = GONE
                binding.hymnBar.layoutHymn.visibility = VISIBLE
                binding.hymnBar.lyricsCategory.visibility = VISIBLE
                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_tagalog_title)
                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_tagalog_lyrics)
//                binding.hymnBar.lyricsCategory.setOnClickListener {
//                    setLyricsOptions()
//                }
                var lyrics: String? = "Tagalog Hymn"
                binding.hymnBar.lyricsCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        when (tab.position) {
                            0 -> {
                                lyrics = "Tagalog Hymn"
                                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_tagalog_title)
                                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_tagalog_lyrics)
                            }
                            1 -> {
                                lyrics = "Chabacano Hymn"
                                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_chabacano_title)
                                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_chabacano_lyrics)
                            }
                            else -> {

                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {
                        mediaPlayerHelper.stopMusic()
                    }
                    override fun onTabReselected(tab: TabLayout.Tab) {
                        mediaPlayerHelper.stopMusic()
                    }
                })
                binding.hymnBar.playSound.setOnClickListener {
                    mediaPlayerHelper.playMusic(lyrics!!){
                        if (it) mediaPlayerHelper.stopMusic()
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerHelper.stopMusic()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayerHelper.stopMusic()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerHelper.stopMusic()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayerHelper.stopMusic()
    }

    val fonts = arrayOf(
        "TAGALOG LYRICS", "CHABACANO LYRICS"
    )
//    private fun setLyricsOptions() {
//        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//        builder.setTitle("Select translation")
//        builder.setItems(fonts, DialogInterface.OnClickListener { dialog, which ->
//            if ("TAGALOG LYRICS" == fonts[which]) {
//                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_tagalog_title)
//                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_tagalog_lyrics)
//            } else if ("CHABACANO LYRICS" == fonts[which]) {
//                binding.hymnBar.hymnTitle.text = getString(R.string.hymn_chabacano_title)
//                binding.hymnBar.hymnLyrics.text = getString(R.string.hymn_chabacano_lyrics)
//            }
//        })
//        builder.show()
//    }

    private fun setBindingSchool() {
        binding.elementary.setOnClickListener {
            setRvCate("Elementary")
            listenToByCatSearch("Elementary")
        }
        binding.highschool.setOnClickListener {
            setRvCate("Highschool")
            listenToByCatSearch("Highschool")
        }
        binding.college.setOnClickListener {
            setRvCate("College")
            listenToByCatSearch("College")
        }
        binding.vocational.setOnClickListener {
            setRvCate("Vocational")
            listenToByCatSearch("Vocational")
        }
        binding.other.setOnClickListener {
            setRvCate("Other")
            listenToByCatSearch("Other")
        }
    }


    private fun setBindingHistory() {
        binding.heroes.setOnClickListener {
            setRvCate("Heroes")
            listenToByCatSearch("Heroes")
        }
        binding.historical.setOnClickListener {
            setRvCate("Historical Places")
            listenToByCatSearch("Historical Places")
        }
        binding.events.setOnClickListener {
            setRvCate("Events")
            listenToByCatSearch("Events")
        }
    }

    private fun setBindingHotel() {
        binding.resort.setOnClickListener {
            setRvCate("Resort")
            listenToByCatSearch("Resort")
        }
        binding.hotel.setOnClickListener {
            setRvCate("Hotel")
            listenToByCatSearch("Hotel")
        }
    }

    private fun setBindingContacts() {
        binding.hospital.setOnClickListener {
            setRvCate("Hospital")
            listenToByCatSearch("Hospital")
        }
        binding.healthCenter.setOnClickListener {
            setRvCate("Health Center")
            listenToByCatSearch("Health Center")
        }
        binding.pharmacy.setOnClickListener {
            setRvCate("Pharmacy")
            listenToByCatSearch("Pharmacy")
        }
        binding.police.setOnClickListener {
            setRvCate("Police Station")
            listenToByCatSearch("Police Station")
        }
        binding.fire.setOnClickListener {
            setRvCate("Fire Station")
            listenToByCatSearch("Fire Station")
        }
    }

    private fun setBindingFoods() {
        binding.cafes.setOnClickListener {
            setRvCate("Cafes")
            listenToByCatSearch("Cafes")
        }
//        binding.stores.setOnClickListener {
//            setRvCate("Convenience Stores")
//            listenToByCatSearch("Convenience Stores")
//        }
        binding.fastfood.setOnClickListener {
            setRvCate("Fast Food")
            listenToByCatSearch("Fast Food")
        }
        binding.homemade.setOnClickListener {
            setRvCate("Homemade Foods")
            listenToByCatSearch("Homemade Foods")
        }
        binding.restaurant.setOnClickListener {
            setRvCate("Restaurants")
            listenToByCatSearch("Restaurants")
        }
        binding.delicacies.setOnClickListener {
            setRvCate("Famous Delicacies")
            listenToByCatSearch("Famous Delicacies")
        }
    }

    private fun listenToByCatSearch(category: String) {
        binding.svEvents.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    binding.progressBar.visibility = VISIBLE
                    try {
                        val responseLiveData = eventViewModel.searchCategory(query, name,category)
                        responseLiveData.observe(this@RvEventActivity, Observer {
                            when(it){
                                is Result.Success<*> -> {
                                    val result = it.data as List<AllModelOutput>
                                    if (result!=null){
                                        allModelOutput.clear()
                                        allModelOutput.addAll(result)
                                        val llm = LinearLayoutManager(this@RvEventActivity, RecyclerView.VERTICAL, false)
                                        binding.epoxyRvAdd.layoutManager = llm
                                        binding.epoxyRvAdd.adapter = eventAdapter
                                        binding.progressBar.visibility = GONE
                                        eventAdapter.setSuggestions(result)
                                    }else{
                                        binding.noData.visibility= VISIBLE
                                        binding.epoxyRvAdd.visibility= GONE
                                        binding.progressBar.visibility = GONE
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
                                        binding.progressBar.visibility = GONE
                                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                                            positiveButtonFunction = {
                                            })
                                    }
                                }
                                Result.Loading -> {
                                    binding.progressBar.visibility = VISIBLE
                                }

                                else -> {}
                            }
                        })
                    } catch (e: IOException) {
                        e.printStackTrace()
                        binding.noData.visibility= VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.progressBar.visibility = GONE
                    }
                }else{
                    listenToByCatSearch(category)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateByCatSuggestions(newText, category)
                return true
            }
        })
    }

    private fun updateByCatSuggestions(newText: String?, category: String) {
        if (newText.isNullOrEmpty()) {
            setCategory(name)
            return
        }
        binding.epoxyRvAdd.visibility = FrameLayout.VISIBLE

        try {
            val responseLiveData = eventViewModel.searchCategory(newText, name,category)
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        val result = it.data as List<AllModelOutput>
                        if (result!=null){
                            if (result.isEmpty()){
                                binding.progressBar.visibility = GONE
                                binding.noData.visibility= VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                binding.noData.text = "Sorry no data about $newText!"
                            }else{
                                allModelOutput.clear()
                                allModelOutput.addAll(result)
                                val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                                binding.epoxyRvAdd.layoutManager = llm
                                binding.epoxyRvAdd.adapter = eventAdapter
                                binding.progressBar.visibility = GONE
                                binding.noData.visibility = GONE
                                eventAdapter.setSuggestions(result)
                            }
                        }else{
                            binding.progressBar.visibility = GONE
                            binding.noData.visibility= VISIBLE
                            binding.epoxyRvAdd.visibility= GONE
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
                            binding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                                positiveButtonFunction = {

                                })
                        }
                    }
                    Result.Loading -> {
                        binding.progressBar.visibility = VISIBLE
                    }

                    else -> {}
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
            binding.progressBar.visibility = GONE
            binding.noData.visibility= VISIBLE
            binding.epoxyRvAdd.visibility= GONE
        }
    }

    private fun setRvCate(category: String){
        if (category!=null){
            val responseLiveData = eventViewModel.getCategory(category)
            try {
                responseLiveData.observe(this, Observer {
                    when(it){
                        is Result.Success<*> -> {
                            val result = it.data as List<AllModelOutput>
                            if (result!=null){
                                if (result.isEmpty()){
                                    binding.noData.visibility=VISIBLE
                                    binding.epoxyRvAdd.visibility= GONE
                                    binding.labelWelcome.text = "No list for $category"
                                    Log.e("EPOXY", "EMPTY 1")
                                }else{
                                    allModelOutput.clear()
                                    allModelOutput.addAll(result)
                                    val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                                    binding.epoxyRvAdd.layoutManager = llm
                                    binding.epoxyRvAdd.adapter = eventAdapter
                                    binding.labelWelcome.text = "Category $category"
                                    binding.noData.visibility=GONE
                                    binding.epoxyRvAdd.visibility= VISIBLE
                                    eventAdapter.notifyDataSetChanged()
                                    Log.d("EPOXY", "NOT EMPTY 1")
                                }
                            }else{
                                Log.e("EPOXY", "EMPTY 2")
                                binding.noData.visibility=VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                Log.d("RvEvent", "Null")
                            }
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
                                // Handle other exceptions
                                binding.progressBar.visibility = GONE
                                dialogHelper.showUnauthorized(
                                    Error(
                                        "Error",
                                        "Something went wrong!"
                                    ),
                                    positiveButtonFunction = {
                                    }
                                )
                            }
                        }
                        Result.Loading -> {

                        }
                    }
                })
            }catch (e: Exception){
                binding.noData.visibility=VISIBLE
                binding.epoxyRvAdd.visibility= GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.noData.visibility= GONE
        Log.d("RvEvent", name)
        Log.d("eventID", "onResume")
        initRv()
        binding.rrMid.visibility = GONE
        when(name){
            "History", "Foods","Hotel & Resorts","Emergency Care & Contacts", "Schools" -> {
                binding.rrMid.visibility = VISIBLE
                setCategory(name)
            }
        }
    }
    private fun setMenu() {
        binding.loggedInTopNav.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("eventID", "onStart")
        binding.noData.visibility= GONE
        Log.d("RvEvent", name)
        initRv()
        setCategory(name)
    }

    private fun initRv() {
        val responseLiveData = eventViewModel.getEventsByCategory(name)
        try {
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        val result = it.data as List<AllModelOutput>
                        if (result!=null){
                            if (result.isEmpty()){
                                binding.noData.visibility=VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                binding.labelWelcome.text = "No list for $name"
                            }else{
                                allModelOutput.clear()
                                allModelOutput.addAll(result)
                                val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                                binding.epoxyRvAdd.layoutManager = llm
                                binding.epoxyRvAdd.adapter = eventAdapter
                                binding.labelWelcome.text = "Category all"
                                eventAdapter.notifyDataSetChanged()
                            }
                        }else{
                            binding.noData.visibility=VISIBLE
                            binding.epoxyRvAdd.visibility= GONE
                            Log.d("RvEvent", "Null")
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
                            binding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                                positiveButtonFunction = {

                                })
                        }
                    }
                    Result.Loading -> {
                        binding.progressBar.visibility = VISIBLE
                    }

                    else -> {}
                }
            })
        }catch (e: Exception){
            binding.noData.visibility=VISIBLE
            binding.epoxyRvAdd.visibility= GONE
        }
    }
    private fun listenToSearch() {
        binding.svEvents.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    binding.progressBar.visibility = VISIBLE
                    try {
                        val responseLiveData = eventViewModel.searchEvents(query, name)
                        responseLiveData.observe(this@RvEventActivity, Observer {
                            when(it){
                                is Result.Success<*> -> {
                                    val result = it.data as List<AllModelOutput>
                                    if (result!=null){
                                        allModelOutput.clear()
                                        allModelOutput.addAll(result)
                                        val llm = LinearLayoutManager(this@RvEventActivity, RecyclerView.VERTICAL, false)
                                        binding.epoxyRvAdd.layoutManager = llm
                                        binding.epoxyRvAdd.adapter = eventAdapter
                                        binding.progressBar.visibility = GONE
                                        eventAdapter.setSuggestions(result)
                                    }else{
                                        binding.noData.visibility= VISIBLE
                                        binding.epoxyRvAdd.visibility= GONE
                                        binding.progressBar.visibility = GONE
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
                                        binding.progressBar.visibility = GONE
                                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                                            positiveButtonFunction = {

                                            })
                                    }
                                }
                                Result.Loading -> {
                                    binding.progressBar.visibility = VISIBLE
                                }

                                else -> {}
                            }
                        })
                    } catch (e: IOException) {
                        e.printStackTrace()
                        binding.noData.visibility= VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.progressBar.visibility = GONE
                    }
                }else{
                    Log.d("search","else")
                    initRv()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("search","onQueryTextChange")
                updateSuggestions(newText)
                return true
            }
        })
    }

    private fun updateSuggestions(newText: String?) {
        if (newText.isNullOrEmpty()) {
            Log.d("search","if (newText.isNullOrEmpty())")
            initRv()
            return
        }
        binding.epoxyRvAdd.visibility = FrameLayout.VISIBLE

        try {
            val responseLiveData = eventViewModel.searchEvents(newText, name)
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        Log.d("search","Success")
                        val result = it.data as List<AllModelOutput>
                        if (result!=null){
                            if (result.isEmpty()){
                                binding.progressBar.visibility = GONE
                                binding.noData.visibility= VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                binding.noData.text = "Sorry no data about $newText!"
                            }else{
                                allModelOutput.clear()
                                allModelOutput.addAll(result)
                                val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                                binding.epoxyRvAdd.layoutManager = llm
                                binding.epoxyRvAdd.adapter = eventAdapter
                                binding.progressBar.visibility = GONE
                                binding.noData.visibility = GONE
                                eventAdapter.setSuggestions(result)
                            }
                        }else{
                            binding.progressBar.visibility = GONE
                            binding.noData.visibility= VISIBLE
                            binding.epoxyRvAdd.visibility= GONE
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
                            binding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                                positiveButtonFunction = {

                                })
                        }
                    }
                    Result.Loading -> {
                        binding.progressBar.visibility = VISIBLE
                    }

                    else -> {}
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
            binding.progressBar.visibility = GONE
            binding.noData.visibility= VISIBLE
            binding.epoxyRvAdd.visibility= GONE
        }
    }
}