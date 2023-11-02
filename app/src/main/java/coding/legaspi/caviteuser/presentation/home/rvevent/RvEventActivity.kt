package coding.legaspi.caviteuser.presentation.home.rvevent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput
import coding.legaspi.caviteuser.databinding.ActivityRvEventBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.adapter.EventAdapter
import coding.legaspi.caviteuser.presentation.menu.MenuActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRvEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        name = intent.getStringExtra("name").toString()
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        allModelOutput = arrayListOf()
        eventAdapter = EventAdapter(allModelOutput, this)

        setMenu()
        initRv()
        listenToSearch()
    }

    private fun setCategory(name: String) {
        binding.llHistory.visibility = GONE
        binding.llContacts.visibility = GONE
        binding.llPlaces.visibility = GONE
        binding.llFoods.visibility = GONE
        when(name){
            "History" -> {
                binding.llHistory.visibility = VISIBLE
                setBindingHistory()
            }
            "Foods" -> {
                binding.llFoods.visibility = VISIBLE
                setBindingFoods()
            }
            "Places To visit" -> {
                binding.llPlaces.visibility = VISIBLE
                setBindingVisit()
            }
            "Emergency Care & Contacts" -> {
                binding.llContacts.visibility = VISIBLE
                setBindingContacts()
            }
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

    private fun setBindingVisit() {
        binding.park.setOnClickListener {
            setRvCate("Park")
            listenToByCatSearch("Park")
        }
        binding.moralla.setOnClickListener {
            setRvCate("Moralla")
            listenToByCatSearch("Moralla")
        }
    }

    private fun setBindingContacts() {
        binding.hospital.setOnClickListener {
            setRvCate("Hospital")
            listenToByCatSearch("Hospital")
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
        binding.stores.setOnClickListener {
            setRvCate("Convenience Stores")
            listenToByCatSearch("Convenience Stores")
        }
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
                            if (it!=null){
                                allModelOutput.clear()
                                allModelOutput.addAll(it)
                                val llm = LinearLayoutManager(this@RvEventActivity, RecyclerView.VERTICAL, false)
                                binding.epoxyRvAdd.layoutManager = llm
                                binding.epoxyRvAdd.adapter = eventAdapter
                                binding.progressBar.visibility = GONE
                                eventAdapter.setSuggestions(it)
                            }else{
                                binding.noData.visibility= VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                binding.progressBar.visibility = GONE
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
                if (it!=null){
                    if (it.isEmpty()){
                        binding.progressBar.visibility = GONE
                        binding.noData.visibility= VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.noData.text = "Sorry no data about $newText!"
                    }else{
                        allModelOutput.clear()
                        allModelOutput.addAll(it)
                        val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        binding.epoxyRvAdd.layoutManager = llm
                        binding.epoxyRvAdd.adapter = eventAdapter
                        binding.progressBar.visibility = GONE
                        binding.noData.visibility = GONE
                        eventAdapter.setSuggestions(it)
                    }
                }else{
                    binding.progressBar.visibility = GONE
                    binding.noData.visibility= VISIBLE
                    binding.epoxyRvAdd.visibility= GONE
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
                    if (it!=null){
                        if (it.isEmpty()){
                            binding.noData.visibility=VISIBLE
                            binding.epoxyRvAdd.visibility= GONE
                            binding.labelWelcome.text = "No list for $category"
                            Log.e("EPOXY", "EMPTY 1")
                        }else{
                            allModelOutput.clear()
                            allModelOutput.addAll(it)
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
        initRv()
        binding.rrMid.visibility = GONE
        when(name){
            "History", "Foods","Places To visit","Emergency Care & Contacts"  -> {
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
        binding.noData.visibility= GONE
        Log.d("RvEvent", name)
        initRv()
        setCategory(name)
    }

    private fun initRv() {
        val responseLiveData = eventViewModel.getEventsByCategory(name)
        try {
            responseLiveData.observe(this, Observer {
                if (it!=null){
                    if (it.isEmpty()){
                        binding.noData.visibility=VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.labelWelcome.text = "No list for $name"
                    }else{
                        allModelOutput.clear()
                        allModelOutput.addAll(it)
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
                            if (it!=null){
                                allModelOutput.clear()
                                allModelOutput.addAll(it)
                                val llm = LinearLayoutManager(this@RvEventActivity, RecyclerView.VERTICAL, false)
                                binding.epoxyRvAdd.layoutManager = llm
                                binding.epoxyRvAdd.adapter = eventAdapter
                                binding.progressBar.visibility = GONE
                                eventAdapter.setSuggestions(it)
                            }else{
                                binding.noData.visibility= VISIBLE
                                binding.epoxyRvAdd.visibility= GONE
                                binding.progressBar.visibility = GONE
                            }
                        })
                    } catch (e: IOException) {
                        e.printStackTrace()
                        binding.noData.visibility= VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.progressBar.visibility = GONE
                    }
                }else{
                    initRv()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateSuggestions(newText)
                return true
            }
        })
    }

    private fun updateSuggestions(newText: String?) {
        if (newText.isNullOrEmpty()) {
            initRv()
            return
        }
        binding.epoxyRvAdd.visibility = FrameLayout.VISIBLE

        try {
            val responseLiveData = eventViewModel.searchEvents(newText, name)
            responseLiveData.observe(this, Observer {
                if (it!=null){
                    if (it.isEmpty()){
                        binding.progressBar.visibility = GONE
                        binding.noData.visibility= VISIBLE
                        binding.epoxyRvAdd.visibility= GONE
                        binding.noData.text = "Sorry no data about $newText!"
                    }else{
                        allModelOutput.clear()
                        allModelOutput.addAll(it)
                        val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        binding.epoxyRvAdd.layoutManager = llm
                        binding.epoxyRvAdd.adapter = eventAdapter
                        binding.progressBar.visibility = GONE
                        binding.noData.visibility = GONE
                        eventAdapter.setSuggestions(it)
                    }
                }else{
                    binding.progressBar.visibility = GONE
                    binding.noData.visibility= VISIBLE
                    binding.epoxyRvAdd.visibility= GONE
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