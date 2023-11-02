package coding.legaspi.caviteuser.presentation.home.adapter

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.rating.RatingOutput
import coding.legaspi.caviteuser.utils.FirebaseManager
import com.bumptech.glide.Glide
import com.kaelli.niceratingbar.NiceRatingBar
import com.kaelli.niceratingbar.RatingStatus

class RatingAdapter(
    private val ratingList: ArrayList<RatingOutput>,
    private val context: Context
): RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_rating, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = ratingList[position].id
        val eventid = ratingList[position].eventid
        val userid = ratingList[position].userid
        val review = ratingList[position].review
        val date = ratingList[position].date
        val rate = ratingList[position].rate
        val name = ratingList[position].name

        holder.label_name.text = name
        holder.rating.setRating(rate.toFloat())
        holder.rating.setRatingStatus(RatingStatus.Disable)
        holder.label_review.text = review
        holder.label_date.text = date
        holder.label_rate.text = rate.toString()
        setProfile(userid, holder)
    }

    private fun setProfile(userid: String, holder: ViewHolder) {
        FirebaseManager().fetchProfileFromFirebase(userid){
            if (it!=null){
                try {
                    Glide.with(context)
                        .load(it.imageUri)
                        .placeholder(R.drawable.baseline_broken_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(holder.imgProfile)
                }catch (e: Exception){
                    Log.e("ImageAdapter", "rv Image $e")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val rating: NiceRatingBar = itemView.findViewById(R.id.rating)
        val label_date: TextView = itemView.findViewById(R.id.label_date)
        val label_review: TextView = itemView.findViewById(R.id.label_review)
        val label_rate : TextView = itemView.findViewById(R.id.label_rate)
        val label_name : TextView = itemView.findViewById(R.id.label_name)

    }
}