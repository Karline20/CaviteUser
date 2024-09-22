package coding.legaspi.caviteuser.presentation.favorites.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.presentation.favorites.FavoriteImage
import com.bumptech.glide.Glide

class FavoriteImageAdapter(
    private val imageList: ArrayList<FavoriteImage>,
    private val context: Context,
): RecyclerView.Adapter<FavoriteImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteImageAdapter.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.epoxy_image, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.clearImageView()
        val images = imageList[position]
        val imageUri = images.imageUri
        var id = images.id
        val eventId = images.eventID
        val timestamp = images.timestamp
        val image = Uri.parse(imageUri)
        Glide.with(context)
            .load(image)
            .placeholder(R.drawable.baseline_broken_image_24)
            .error(R.drawable.baseline_broken_image_24)
            .into(holder.imgcafe)
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