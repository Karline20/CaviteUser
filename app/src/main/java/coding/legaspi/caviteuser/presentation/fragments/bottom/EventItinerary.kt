package coding.legaspi.caviteuser.presentation.fragments.bottom

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.local.ItineraryEntity
import coding.legaspi.caviteuser.data.local.ItineraryViewModel
import coding.legaspi.caviteuser.data.model.itenerary.Itinerary
import coding.legaspi.caviteuser.databinding.FragmentEventItineraryBinding
import coding.legaspi.caviteuser.presentation.home.map.MapActivity
import coding.legaspi.caviteuser.utils.DateUtils
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import coding.legaspi.caviteuser.utils.alarm.AlarmManagerUtil
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [EventItinerary.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventItinerary(
    val itinerary: Itinerary,
    val eventName: String,
    val latitude: String,
    val longitude: String,
    val currentLocation: Location?,
    val dialogHelper: DialogHelper,
    val itineraryViewModel: ItineraryViewModel,
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEventItineraryBinding
    private var _dialogHelper = dialogHelper
    private var _itineraryViewModel = itineraryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEventItineraryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = GONE
        binding.tripName.text = eventName
        binding.dateSched.text = DateUtils.formatTimestampToDate(itinerary.scheduleDateTimestamp.toLong())
        binding.timeSched.text = DateUtils.formatTimestampToTime(itinerary.scheduleDateTimestamp.toLong())
        binding.destinationAddress.text = itinerary.itineraryPlace
        binding.currentAddress.text = getAddressName(requireContext(), currentLocation)

        Log.i("CHECK ITINERARY", "Image ${itinerary.itineraryImg}")
        val reference = FirebaseDatabase.getInstance().getReference("Itinerary")
        reference.child(itinerary.userID).child(itinerary.eventID).child(itinerary.itineraryID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists() && snapshot.child("tripStatus").value == "TRIP_STARTED"){
                        binding.endTrip.visibility = VISIBLE
                        binding.endTrip.isEnabled = true
                        binding.startTrip.visibility = GONE
                        binding.startTrip.isEnabled = false
                    }else{
                        binding.startTrip.visibility = VISIBLE
                        binding.startTrip.isEnabled = true
                        binding.endTrip.visibility = GONE
                        binding.endTrip.isEnabled = false
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                }
            })

        try {
            Glide.with(requireContext())
                .load(itinerary.itineraryImg)
                .placeholder(R.drawable.baseline_broken_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .into(binding.tripImage)
        }catch (e: Exception){
            Log.i("Fragment", "Image $e")
        }

        binding.showRoute.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("location", itinerary.itineraryPlace)

            startActivity(intent)
        }

        binding.startTrip.setOnClickListener {
            _dialogHelper.tutorial(
                "Start Trip",
                "Do you want to start your trip?",
                "Yes",
                "No"
            ) {
                if (it){
                    updateItineraryFirebase()
                }
            }
        }

        binding.endTrip.setOnClickListener {
            val address = binding.currentAddress.text.toString()
            _dialogHelper.tutorial(
                "End Trip",
                "Do you want to end your trip?",
                "Yes",
                "No"
            ) {
                if (it){
                    binding.progressBar.visibility = VISIBLE
                    val itinerary = Itinerary(
                        itinerary.timestamp, itinerary.eventID, itinerary.userID, itinerary.itineraryID,
                        itinerary.scheduleDateTimestamp, itinerary.timestamp, "TRIP_ENDED", true, DateUtils.getCurrentTimestamp(),
                        itinerary.itineraryImg, itinerary.itineraryPlace, itinerary.itineraryName, address)
                    FirebaseManager().historyItinerary(itinerary){
                        if (it){
                            FirebaseManager().delItinerary(itinerary.userID, itinerary.eventID, itinerary.itineraryID){
                                if (it){
                                    val alarm = ItineraryEntity(itinerary.itineraryID.toLong(), itinerary.eventID, itinerary.scheduleDateTimestamp.toLong(),
                                        itinerary.timestamp, itinerary.itineraryName, itinerary.itineraryPlace, itinerary.itineraryImg)
                                    AlarmManagerUtil.cancelAlarm(requireContext(), alarm)
                                    binding.progressBar.visibility = GONE
                                    _itineraryViewModel.delete(itinerary.itineraryID.toLong())
                                    Toast.makeText(requireContext(), "Trip ended!", Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.beginTransaction().remove(this).commit()
                                }else{
                                    binding.progressBar.visibility = GONE
                                    Toast.makeText(context, "Trip ended failed!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            binding.progressBar.visibility = GONE
                            Toast.makeText(context, "Trip ended failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

    fun getAddressName(context: Context, location: Location?): String? {
        location?.let {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses!!.isNotEmpty()) {
                    val address = addresses[0]
                    return address.getAddressLine(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null // Return null if the address cannot be retrieved
    }

    fun updateItineraryFirebase(){
        binding.progressBar.visibility = VISIBLE
        val address = binding.currentAddress.text.toString()
        FirebaseManager().udpateItinerary(
            itinerary.userID,
            itinerary.eventID,
            itinerary.itineraryID,
            "TRIP_STARTED",
            false,
            address){
            if (it){
                binding.progressBar.visibility = GONE
                Toast.makeText(requireContext(), "Enjoy your trip!", Toast.LENGTH_SHORT).show()
            }else{
                binding.progressBar.visibility = GONE
                Toast.makeText(requireContext(), "Starting trip failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}