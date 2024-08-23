package coding.legaspi.caviteuser.presentation.itinerary

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.itenerary.Itinerary
import coding.legaspi.caviteuser.presentation.fragments.bottom.HistoryItineraryFragment
import coding.legaspi.caviteuser.utils.DateUtils
import com.bumptech.glide.Glide

class ItineraryHistoryAdapter(
    private val context: Context,
    private val itineraryList: ArrayList<Itinerary>,
    private val supportFragmentManager: FragmentManager,
): RecyclerView.Adapter<ItineraryHistoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tripImage: ImageView = itemView.findViewById(R.id.tripImage)
        val dateSched: TextView = itemView.findViewById(R.id.dateSched)
        val timeSched: TextView = itemView.findViewById(R.id.timeSched)
        val isCompleted: TextView = itemView.findViewById(R.id.isCompleted)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.epoxy_itinerary_history_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = itineraryList[position].id
        val eventID = itineraryList[position].eventID
        val userID = itineraryList[position].userID
        val itineraryID = itineraryList[position].itineraryID
        val scheduleDateTimestamp = itineraryList[position].scheduleDateTimestamp
        val timestamp = itineraryList[position].timestamp
        val tripStatus = itineraryList[position].tripStatus
        val isTripCompleted = itineraryList[position].isTripCompleted
        val dateCompleted = itineraryList[position].dateCompleted
        val itineraryImg = itineraryList[position].itineraryImg
        val itineraryPlace = itineraryList[position].itineraryPlace
        val itineraryName = itineraryList[position].itineraryName
        val itineraryFrom = itineraryList[position].itineraryFrom

        Log.e("HISTORYADAPTER", "isTripCompleted $isTripCompleted")
        if (isTripCompleted) {
            holder.isCompleted.text = "COMPLETED"
            holder.isCompleted.setTextColor(context.resources.getColor(R.color.successGreen))
        }

        holder.dateSched.text = DateUtils.formatTimestampToDate(scheduleDateTimestamp.toLong())
        holder.timeSched.text = DateUtils.formatTimestampToTime(scheduleDateTimestamp.toLong())

        try {
            Glide.with(context)
                .load(itineraryImg)
                .placeholder(R.drawable.baseline_broken_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .into(holder.tripImage)
        }catch (e: Exception){
            Log.e("ImageAdapter", "rv Image $e")
        }

        holder.itemView.setOnClickListener {
            val itinerary = Itinerary(
                id, eventID, userID, itineraryID,
            scheduleDateTimestamp, timestamp, tripStatus, isTripCompleted, dateCompleted,
            itineraryImg, itineraryPlace, itineraryName, itineraryFrom)

            val bottomEventItinerary = HistoryItineraryFragment(itinerary)
            bottomEventItinerary.show(supportFragmentManager, bottomEventItinerary.tag)
        }
    }

    override fun getItemCount(): Int {
        return itineraryList.size
    }
}