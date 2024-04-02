package coding.legaspi.caviteuser.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.addevents.AddEvent
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput
import coding.legaspi.caviteuser.databinding.ActivityHomeBinding
import coding.legaspi.caviteuser.presentation.about.AboutActivity
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.adapter.EventAdapter
import coding.legaspi.caviteuser.presentation.home.adapter.HomeAdapter
import coding.legaspi.caviteuser.presentation.menu.MenuActivity
import coding.legaspi.caviteuser.presentation.play.PlayActivity
import coding.legaspi.caviteuser.presentation.tutorial.TutorialActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import javax.inject.Inject
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.error.Error
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var dialogHelper: DialogHelper

    private lateinit var eventList: ArrayList<AddEvent>
    private lateinit var adapter: HomeAdapter
    private lateinit var eventAdapter: EventAdapter
    private lateinit var allModelOutput: ArrayList<AllModelOutput>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        allModelOutput = arrayListOf()
        eventAdapter = EventAdapter(allModelOutput, this)
        eventList = arrayListOf()
        adapter = HomeAdapter(this,eventList, this, eventViewModel)
        binding.progressBar.visibility = VISIBLE

        setProfile()
        setBottomButton()
        setMenu()
    }

    private fun setMenu() {
        binding.loggedInTopNav.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setBottomButton() {
        binding.loggedInBottomNav.home.setImageResource(R.drawable.home_red)

        binding.loggedInBottomNav.tutorial.setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
        }
        binding.loggedInBottomNav.play.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
        }
        binding.loggedInBottomNav.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
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
                        .into(binding.loggedInTopNav.imgProfile)
                    initRv()
                }else{
                    initRv()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setProfile()
    }

    private fun initRv() {
        binding.progressBar.visibility = VISIBLE

        val responseLiveData = eventViewModel.getAddEvents()
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as List<AddEvent>
                    if (result!=null){
                        if (result.isEmpty()){
                            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                            binding.noData.visibility= VISIBLE
                            binding.rvEvents.visibility= GONE
                            binding.progressBar.visibility = GONE
                        }else{
                            eventList.clear()
                            eventList.addAll(result)
                            binding.rvEvents.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                            binding.rvEvents.adapter = adapter
                            adapter.notifyDataSetChanged()
                            binding.progressBar.visibility = GONE
                            binding.noData.visibility= GONE
                        }
                    }else{
                        Log.d("Home", "Null")
                    }
                }
                is Result.Error -> {
                    val exception = it.exception

                    if (exception is IOException) {
                        binding.progressBar.visibility = GONE
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(Error("Server error", "Server is down or not reachable ${exception.message}"))
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
//        val responseLiveData = eventViewModel.getAllEvents()
//        try {
//            responseLiveData.observe(this, Observer {
//                if (it!=null){
//                    if(it.isEmpty()){
//                        binding.noData.visibility= VISIBLE
//                        binding.rvEvents.visibility= GONE
//                        binding.progressBar.visibility = GONE
//                    }else{
//                        allModelOutput.clear()
//                        allModelOutput.addAll(it)
//                        val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//                        binding.rvEvents.layoutManager = llm
//                        binding.rvEvents.adapter = eventAdapter
//                        binding.progressBar.visibility = GONE
//                        binding.noData.visibility=GONE
//                        eventAdapter.notifyDataSetChanged()
//                    }
//                }else{
//                    binding.noData.visibility= VISIBLE
//                    binding.rvEvents.visibility= GONE
//                }
//            })
//        }catch (e: Exception){
//            binding.noData.visibility= View.VISIBLE
//            binding.rvEvents.visibility= View.GONE
//        }
    }


}