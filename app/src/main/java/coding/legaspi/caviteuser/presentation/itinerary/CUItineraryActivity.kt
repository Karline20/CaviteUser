package coding.legaspi.caviteuser.presentation.itinerary

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.local.ItineraryEntity
import coding.legaspi.caviteuser.data.local.ItineraryViewModel
import coding.legaspi.caviteuser.data.model.itenerary.Itinerary
import coding.legaspi.caviteuser.databinding.ActivityCuitineraryBinding
import coding.legaspi.caviteuser.utils.DateUtils
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import coding.legaspi.caviteuser.utils.alarm.AlarmManagerUtil
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class CUItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuitineraryBinding
    private lateinit var itineraryViewModel: ItineraryViewModel
    private var id: Long = 0
    private lateinit var eventid: String
    private lateinit var eventName: String
    private lateinit var location: String
    private lateinit var firstImage: String
    private var scheduleDateTimestamp: Long = 0
    private lateinit var timestamp: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuitineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        itineraryViewModel = ViewModelProvider(this).get(ItineraryViewModel::class.java)

        binding.loggedInTopNav.labelTitle.text = "ITINERARY PLANNER"
        eventid = intent.getStringExtra("eventid").toString()
        eventName = intent.getStringExtra("eventName").toString()
        location = intent.getStringExtra("location").toString()
        firstImage = intent.getStringExtra("firstImage").toString()
        val idString = intent.getLongExtra("id", 0L)
        Log.i("CUItineraryActivity", "itinerary $firstImage")

        if (idString != null) {
            id = idString
            Log.i("CUItineraryActivity", "found $id")
        } else {
            Log.i("CUItineraryActivity", "No found $id")
            Log.e("CUItineraryActivity", "No ID found in the Intent")
        }
        val scheduleDateTimestampString = intent.getLongExtra("scheduleDateTimestamp", 0L)
        if (scheduleDateTimestampString != null) {
            scheduleDateTimestamp = scheduleDateTimestampString
            Log.i("CUItineraryActivity", "$scheduleDateTimestamp")
        } else {
            Log.i("CUItineraryActivity", "No found $scheduleDateTimestamp")
            Log.e("CUItineraryActivity", "No ID found in the Intent")
        }
        setActions()
        setProfile()
        binding.txtPlaceName.text = eventName
        binding.txtPlaceAddress.text = location
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
    private fun setActions() {
        binding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
        }

        binding.datePicker.setOnClickListener {
            datePickerDialog()
        }

        binding.timePicker.setOnClickListener {
            timePickerDialog()
        }
        binding.btnSaveItinerary.setOnClickListener {
            binding.progressBar.visibility = VISIBLE
            val date = binding.txtDate.text.toString()
            val time = binding.txtTime.text.toString()
            val itineraryName = binding.txtPlaceName.text.toString()
            val itineraryPlace = binding.txtPlaceAddress.text.toString()
            val scheduleDateTime = "$date $time"
            val scheduled = if (scheduleDateTimestamp == null || scheduleDateTimestamp == 0L) {
                DateUtils.parseDateToTimestamp(scheduleDateTime)
            } else {
                scheduleDateTimestamp
            }
            //val itineraryModel = ItineraryEntity(id ?: 0L, eventid, scheduled, DateUtils.getCurrentTimestamp(), itineraryName, itineraryPlace)
            Log.i("CUItineraryActivity", "found clicked $id")
            Log.i("CUItineraryActivity", "itinerary $firstImage")

            val itineraryModel = if (id != null && id != 0L) {
                ItineraryEntity(id, eventid, scheduled, DateUtils.getCurrentTimestamp(), itineraryName, itineraryPlace, firstImage)
            } else {
                ItineraryEntity(0L, eventid, scheduled, DateUtils.getCurrentTimestamp(), itineraryName, itineraryPlace, firstImage)
            }
            if(itineraryModel.id != 0L){
                binding.progressBar.visibility = VISIBLE
                lifecycleScope.launch {
                    try {
                        itineraryViewModel.updateItinerary(id, eventid, scheduled, DateUtils.getCurrentTimestamp(), itineraryName, itineraryPlace)
                    } finally {
                        Toast.makeText(this@CUItineraryActivity, "Update successfully.", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = GONE
                    }
                }
            }else{
                lifecycleScope.launch {
                    try {
                        itineraryViewModel.insertOrUpdate(itineraryModel)
                            .observe(this@CUItineraryActivity, Observer { updatedItinerary  ->
                                if (updatedItinerary  != null || updatedItinerary?.id == 0L) {
                                    Log.e("CUItineraryActivity", "Itinerary : $updatedItinerary ")
                                    Log.e("CUItineraryActivity", "ID : ${updatedItinerary.id} ")
                                    try {
                                        val isSetSuccessfully = AlarmManagerUtil.setAlarm(this@CUItineraryActivity, updatedItinerary)
                                        if (isSetSuccessfully) {
                                            val timestamp = System.currentTimeMillis().toString()
                                            val (token, userId) = SharedPreferences().checkToken(this@CUItineraryActivity)
                                            val itinerary = Itinerary(
                                                timestamp, eventid, userId!!, updatedItinerary.id.toString(),
                                                updatedItinerary.scheduleDateTimestamp.toString(), timestamp, "UPCOMING", false, "", updatedItinerary.itineraryImg, updatedItinerary.itineraryPlace, updatedItinerary.itineraryName, "")
                                            FirebaseManager().addToItinerary(itinerary){
                                                if (it){
                                                    binding.progressBar.visibility = GONE
                                                    Toast.makeText(this@CUItineraryActivity, "Upcoming alarm set successfully.", Toast.LENGTH_SHORT).show()
                                                }else{
                                                    Toast.makeText(this@CUItineraryActivity, "Saving trip failed!", Toast.LENGTH_SHORT).show()
                                                    AlarmManagerUtil.cancelAlarm(this@CUItineraryActivity, updatedItinerary)
                                                    itineraryViewModel.delete(updatedItinerary.id)
                                                }
                                            }
                                        } else {
                                            Log.e("CUItineraryActivity", "Alarm Error")
                                            binding.progressBar.visibility = GONE
                                            Toast.makeText(this@CUItineraryActivity, "Setting alarm error.", Toast.LENGTH_SHORT).show()
                                        }
                                    }catch (e: Exception){
                                        binding.progressBar.visibility = GONE
                                        Log.e("CUItineraryActivity", "Alarm Error $e")
                                    }
                                } else {
                                    binding.progressBar.visibility = GONE
                                    // Handle the case where there was an existing itinerary
                                    Toast.makeText(this@CUItineraryActivity, "You have an existing upcoming trip for the event!", Toast.LENGTH_SHORT).show()
                                }

                            })
                    } finally {
                        Toast.makeText(this@CUItineraryActivity, "Saved to upcoming Trips.", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = GONE
                    }
                }
            }
        }
    }

    private fun datePickerDialog(){

        val year = DateUtils.getCurrentYear()
        val month = DateUtils.getCurrentMonth()

        val day = DateUtils.getCurrentDay()
        Log.i("DATEINFO", "$year , $month , $day")
        val datePickerDialog = DatePickerDialog(this, { _: android.widget.DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthName = months[selectedMonth]
            val selectedDate = "$monthName $selectedDay $selectedYear"
            binding.txtDate.text = selectedDate
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun timePickerDialog(){
        val hour = DateUtils.getCurrentHour()
        val minutes = DateUtils.getCurrentMinute()
        Log.i("TIMEINFO", "$hour , $minutes")
        val timePickerDialog = TimePickerDialog(this,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            { _: android.widget.TimePicker, selectedHour: Int, selectedMinute: Int ->
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hourIn12Format = if (selectedHour == 0 || selectedHour == 12) 12 else selectedHour % 12
                val selectedTime = String.format("%02d:%02d %s", hourIn12Format, selectedMinute, amPm)
                binding.txtTime.text = selectedTime
            }, hour, minutes, false) // true for 24-hour time format, false for 12-hour format
        timePickerDialog.show()
    }
}