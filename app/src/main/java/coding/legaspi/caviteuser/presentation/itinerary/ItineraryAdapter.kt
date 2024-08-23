package coding.legaspi.caviteuser.presentation.itinerary

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import coding.legaspi.caviteuser.data.local.ItineraryEntity
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.local.ItineraryViewModel
import coding.legaspi.caviteuser.presentation.home.event.ViewEventActivity
import coding.legaspi.caviteuser.utils.DateUtils
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import coding.legaspi.caviteuser.utils.alarm.AlarmManagerUtil
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class ItineraryAdapter(
    private val itineraryList: ArrayList<ItineraryEntity>,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val itineraryViewModel: ItineraryViewModel,
    private val progressBar: RelativeLayout,
): RecyclerView.Adapter<ItineraryAdapter.ViewHolder>() {

    private lateinit var dialogHelper: DialogHelper
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val img_del_itinerary: ImageView = itemView.findViewById(R.id.img_del_itinerary)
        val img_schedule: ImageView = itemView.findViewById(R.id.img_schedule)
        val tv_schedule: TextView = itemView.findViewById(R.id.tv_schedule)
        val tv_event_name: TextView = itemView.findViewById(R.id.tv_event_name)
        val tv_address: TextView = itemView.findViewById(R.id.tv_address)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.epoxy_itinerary_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItineraryAdapter.ViewHolder, position: Int) {
        dialogHelper = DialogHelperFactory.create(context)
        val id = itineraryList[position].id
        val eventID = itineraryList[position].eventID
        val scheduleDateTimestamp = itineraryList[position].scheduleDateTimestamp
        val timestamp = itineraryList[position].timestamp
        val itineraryName = itineraryList[position].itineraryName
        val itineraryPlace = itineraryList[position].itineraryPlace
        val itineraryImg = itineraryList[position].itineraryImg
        Log.i("CUItineraryActivity", "itinerary adapter $itineraryImg")

        holder.tv_schedule.text = DateUtils.formatTimestamp(scheduleDateTimestamp)
        holder.tv_event_name.text = itineraryName
        holder.tv_address.text = itineraryPlace
        try {
            Glide.with(context)
                .load(itineraryImg)
                .placeholder(R.drawable.baseline_broken_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .into(holder.img_schedule)
        }catch (e: Exception){
            Log.e("ImageAdapter", "rv Image $e")
        }
        holder.img_del_itinerary.setOnClickListener {
            val (token, userId) = SharedPreferences().checkToken(context)
            dialogHelper.delete("Remove Favorite", "Do you want to remove this from favorites", "Yes", "Cancel"){
                if (it){
                    progressBar.visibility = View.VISIBLE
                    lifecycleOwner.lifecycleScope.launch {
                        try {
                            FirebaseManager().delItinerary(userId.toString(),eventID, id.toString()){
                                if (it){
                                    val alarm = ItineraryEntity(id, eventID, scheduleDateTimestamp, timestamp, itineraryName, itineraryPlace, itineraryImg)
                                    AlarmManagerUtil.cancelAlarm(context, alarm)
                                    itineraryViewModel.delete(id)
                                    Toast.makeText(context, "Delete Successfully!", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "Delete failed!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } finally {
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        holder.img_schedule.setOnClickListener{
            val intent = Intent(context, ViewEventActivity::class.java)
            intent.putExtra("id", eventID)
            context.startActivity(intent)
        }
//
//        holder.img_edit_itinerary.setOnClickListener {
//            val intent = Intent(context, CUItineraryActivity::class.java)
//            intent.putExtra("id", id)
//            intent.putExtra("eventid", eventID)
//            intent.putExtra("eventName", itineraryName)
//            intent.putExtra("location", itineraryPlace)
//            intent.putExtra("firstImage", itineraryImg)
//            intent.putExtra("scheduleDateTimestamp", scheduleDateTimestamp)
//            context.startActivity(intent)
//        }
    }


    override fun getItemCount(): Int {
        return itineraryList.size
    }

}