package coding.legaspi.caviteuser.presentation.home.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View.GONE
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.adaptermodel.Image
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput
import coding.legaspi.caviteuser.presentation.home.event.ViewEventActivity
import coding.legaspi.caviteuser.utils.ImageAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventAdapter(
    private val eventList: ArrayList<AllModelOutput>,
    private val context: Context
): RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    private lateinit var databaseReference: DatabaseReference
    var isCodeExecuted = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_events, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = eventList[position].id
        val name = eventList[position].name
        val category = eventList[position].category
        val description = eventList[position].description
        val date = eventList[position].date
        val latitude = eventList[position].latitude
        val longitude = eventList[position].longitude
        val location = eventList[position].location
        val timestamp = eventList[position].timestamp
        val imageUri = eventList[position].imageuri
        val eventcategory = eventList[position].eventcategory

        val posAndId = "$id,$position"
        holder.label_name.text = name
        holder.label_description.text = description
        holder.label_date.text = date
        if (category!=null){
            if (category=="Heroes") {
                holder.label_category.text = "Notable Person"
            }else{
                holder.label_category.text = category
            }
        }else{
            holder.label_category.visibility = GONE
        }
        holder.txt_address.text = location
        //incrementPos(position, id, holder, posAndId)
        initImage(id, holder, position, posAndId)
//        if (isCodeExecuted){
//            initImage(id, holder, position, posAndId)
//        }
        holder.label_read.setOnClickListener {
            val intent = Intent(context, ViewEventActivity::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }

//    private fun incrementPos(position: Int, id: String, holder: ViewHolder, posAndId: String) {
//        val increment = 1
//        val exactPos = increment+position
//        if(exactPos>=1){
//            isCodeExecuted = true
//            initImage(id, holder, position, posAndId)
//            Log.d("isCodeExecuted", "isCodeExecuted $isCodeExecuted isLoaded False")
//        }
//    }

    private fun initImage(id: String, holder: ViewHolder, position: Int, posAndId: String) {
        val idAndPos = "$id,$position"
        if (idAndPos==(posAndId)){
            lateinit var imageAdapter: ImageAdapter
            val imageList: ArrayList<Image> = arrayListOf()
            imageList.clear()
            databaseReference = FirebaseDatabase.getInstance().getReference("Images")
            databaseReference.child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.d("eventID", "$snapshot $id")
                            imageList.clear()
                            for (snapshot in snapshot.children) {
                                val image = snapshot.getValue(Image::class.java)
                                if (image != null) {
                                    imageList.add(image)
                                    isCodeExecuted = false
                                    Log.d("isCodeExecuted", "isCodeExecuted $isCodeExecuted isLoaded True")
                                }
                            }
                            imageAdapter = ImageAdapter(imageList, context)
                            holder.epoxy_image.adapter = imageAdapter
                            imageAdapter.notifyDataSetChanged()
                        }
                        else {
                            Log.d("error", "error")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun setSuggestions(newSuggestions: List<AllModelOutput>) {
        eventList.clear()
        eventList.addAll(newSuggestions)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label_name: TextView = itemView.findViewById(R.id.label_name)
        val label_category: TextView = itemView.findViewById(R.id.label_category)
        val label_description: TextView = itemView.findViewById(R.id.label_description)
        val txt_address: TextView = itemView.findViewById(R.id.txt_address)
        val label_read: TextView = itemView.findViewById(R.id.label_read)
        val label_date: TextView = itemView.findViewById(R.id.label_date)
        val epoxy_image: RecyclerView = itemView.findViewById(R.id.epoxy_image)

    }

}