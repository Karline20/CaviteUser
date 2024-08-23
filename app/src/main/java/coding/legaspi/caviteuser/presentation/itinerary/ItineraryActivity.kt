package coding.legaspi.caviteuser.presentation.itinerary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.local.ItineraryEntity
import coding.legaspi.caviteuser.data.local.ItineraryViewModel
import coding.legaspi.caviteuser.databinding.ActivityItineraryBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import coding.legaspi.caviteuser.utils.alarm.AlarmManagerUtil
import com.bumptech.glide.Glide
import javax.inject.Inject

class ItineraryActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    lateinit var binding: ActivityItineraryBinding
    private lateinit var itineraryViewModel: ItineraryViewModel
    private lateinit var itineraryAdapter: ItineraryAdapter
    private lateinit var itineraryList: ArrayList<ItineraryEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        itineraryViewModel = ViewModelProvider(this).get(ItineraryViewModel::class.java)
        itineraryList = arrayListOf()
        itineraryAdapter = ItineraryAdapter(itineraryList, this, this, itineraryViewModel, binding.progressBar)
        setButtons()
        setData()
    }

    override fun onResume() {
        super.onResume()
        initItineraryList()
    }

    override fun onStart() {
        super.onStart()
        initItineraryList()
    }
    private fun setData() {
        binding.loggedInTopNav.labelTitle.text = "ITINERARY PLANNER"
    }

    private fun setButtons() {
        binding.loggedInTopNav.back.setOnClickListener { onBackPressed() }

        binding.loggedInTopNav.itineraryHistory.setOnClickListener {
            val intent = Intent(this, ItineraryHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initItineraryList() {
        binding.progressBar.visibility = VISIBLE
        itineraryViewModel.allItineraries.observe(this, Observer {
            if (it.isEmpty()){
                binding.noData.visibility = VISIBLE
                binding.progressBar.visibility = GONE
            }else{
                Log.i("ITINERARY", "List $it")
                binding.noData.visibility = GONE
                itineraryList.clear()
                itineraryList.addAll(it)
                val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
                binding.rvItinerary.layoutManager = llm
                binding.rvItinerary.adapter = itineraryAdapter
                binding.progressBar.visibility = GONE
                itineraryAdapter.notifyDataSetChanged()
            }
            it.forEach { alarm ->
                AlarmManagerUtil.isAlarmSet(this, alarm.id.toInt())
            }
        })
    }
}