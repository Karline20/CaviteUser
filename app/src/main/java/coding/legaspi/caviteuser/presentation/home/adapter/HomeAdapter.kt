package coding.legaspi.caviteuser.presentation.home.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.addevents.AddEvent
import coding.legaspi.caviteuser.presentation.home.rvevent.RvEventActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import java.util.Random

class HomeAdapter internal constructor(
    private val context: Context,
    private val postItems: ArrayList<AddEvent>,
    private val lifecycleOwner: LifecycleOwner,
    private val eventViewModel: EventViewModel
) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.imagePost)
        val txt_number: TextView = itemView.findViewById(R.id.txt_number)
        val label_registered: TextView = itemView.findViewById(R.id.label_registered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_events_icon, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = postItems[position].id
        val name = postItems[position].name

        // Set the background color for each item
        val randomColor = generateRandomColor()
        holder.itemView.setBackgroundColor(randomColor)
        holder.itemView.setBackgroundResource(R.drawable.shape_rec_02)

        holder.label_registered.text = name
        when(name){
            "Cafe"-> holder.postImageView.setImageResource(R.drawable.cafep)
            "Events"-> holder.postImageView.setImageResource(R.drawable.events)
            "Fast-food"-> holder.postImageView.setImageResource(R.drawable.fastp)
            "Festival"-> holder.postImageView.setImageResource(R.drawable.festiva)
            "Foods"-> holder.postImageView.setImageResource(R.drawable.food)
            "Gas Station"-> holder.postImageView.setImageResource(R.drawable.gasstation)
            "Government offices"-> holder.postImageView.setImageResource(R.drawable.gov)
            "History"-> holder.postImageView.setImageResource(R.drawable.historyhis)
            "Home made food"-> holder.postImageView.setImageResource(R.drawable.homej)
            "Restaurants"-> holder.postImageView.setImageResource(R.drawable.restaurant)
            "Hotel & Resorts" -> holder.postImageView.setImageResource(R.drawable.hotelresort)
            "Schools"-> holder.postImageView.setImageResource(R.drawable.schoolp)
            "Stores"-> holder.postImageView.setImageResource(R.drawable.storep)
            "Places To visit"-> holder.postImageView.setImageResource(R.drawable.places)
            "Emergency Care & Contacts"-> holder.postImageView.setImageResource(R.drawable.emergencyp)
        }

        countEvents(name, holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RvEventActivity::class.java)
            intent.putExtra("name", name)
            context.startActivity(intent)
        }
    }

    private fun countEvents(name: String, holder: ViewHolder) {
        val responseLiveData = eventViewModel.countEventsByCategory(name)
        responseLiveData.observe(lifecycleOwner, Observer {
            if (it!=null){
                holder.txt_number.text = it.body()?.count.toString()
            }else{
                holder.txt_number.text = "0"
            }
        })
    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    private fun generateRandomColor(): Int {
        val random = Random()
        // Generate random RGB values
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        // Create a color from RGB values
        return Color.rgb(red, green, blue)
    }

}