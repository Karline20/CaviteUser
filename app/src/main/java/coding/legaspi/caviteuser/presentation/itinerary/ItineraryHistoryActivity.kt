package coding.legaspi.caviteuser.presentation.itinerary

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.data.model.itenerary.Itinerary
import coding.legaspi.caviteuser.databinding.ActivityItineraryHistoryBinding
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItineraryHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItineraryHistoryBinding
    private lateinit var itineraryHistoryAdapter: ItineraryHistoryAdapter
    private lateinit var itineraryList: ArrayList<Itinerary>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItineraryHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        itineraryList = arrayListOf()
        itineraryHistoryAdapter = ItineraryHistoryAdapter(this@ItineraryHistoryActivity,itineraryList, supportFragmentManager)

        binding.loggedInTopNav.itineraryHistory.visibility = GONE
        binding.loggedInTopNav.back.setOnClickListener { onBackPressed() }
        binding.loggedInTopNav.labelTitle.text = "ITINERARY HISTORY"
        loadItineraryHistory()
    }

    private fun loadItineraryHistory() {

        val (token, userId) = SharedPreferences().checkToken(this)
        val itineraryHistory = FirebaseDatabase.getInstance().getReference("ItineraryHistory")
        itineraryHistory.child(userId!!).orderByChild("isTripCompleted").equalTo(true)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.noData.visibility = GONE
                        itineraryList.clear()
                        for (snapshot in snapshot.children){
                            val itineraryHistoryList = snapshot.getValue(Itinerary::class.java)
                            Log.i("ITINERARYHISTORY", "snapshot children ${snapshot.children}")
                            Log.i("ITINERARYHISTORY", "itineraryHistoryList $itineraryHistoryList")
                            //itineraryList.add(itineraryHistoryList!!)
                            itineraryHistoryList?.let { itineraryList.add(it) }

                        }
                        val llm = LinearLayoutManager(this@ItineraryHistoryActivity, RecyclerView.VERTICAL, true)
                        binding.rvItinerary.layoutManager = llm
                        binding.rvItinerary.adapter = itineraryHistoryAdapter
                    }else{
                        binding.noData.visibility = VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ItineraryHistoryActivity, "$error", Toast.LENGTH_SHORT).show()
                }

            })
    }
}