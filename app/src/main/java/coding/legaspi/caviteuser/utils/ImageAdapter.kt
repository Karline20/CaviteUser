package coding.legaspi.caviteuser.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.adaptermodel.Image
import com.bumptech.glide.Glide

class ImageAdapter(
    private val imageList: ArrayList<Image>,
    private val context: Context,
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.epoxy_image, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.clearImageView()
        val images = imageList[position]
        val imageUri = images.imageUri
        var id = images.id
        val eventId = images.eventId
        val userId = images.userId
        val timestamp = images.timestamp
        val image = Uri.parse(imageUri)
        try {
            Glide.with(context)
                .load(image)
                .placeholder(R.drawable.baseline_broken_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .into(holder.imgcafe)
        }catch (e: Exception){
            Log.e("ImageAdapter", "rv Image $e")
        }
        Log.d("ImageAdapter", "rv Image $imageUri")

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgcafe: ImageView = itemView.findViewById(R.id.imgcafe)
        fun clearImageView() {
            imgcafe.setImageDrawable(null) // Clearing the current image
        }
    }


}