package coding.legaspi.caviteuser.presentation.about.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.researchers.ResearchersOutput
import coding.legaspi.caviteuser.utils.FirebaseManager
import com.bumptech.glide.Glide

class AboutAdapter(
    private val researchersList: ArrayList<ResearchersOutput>,
    private val context: Context,
): RecyclerView.Adapter<AboutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.epoxy_researcher, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = researchersList[position].id
        val name = researchersList[position].name
        val rposition = researchersList[position].position
        val address = researchersList[position].address
        val contact = researchersList[position].contact

        holder.txt_name.text = name
        holder.txt_position.text = rposition
        holder.txt_address.text = address
        holder.txt_contact.text = contact
        FirebaseManager().fetchResearchFromFirebase(id){
            val image = it.imageUri.toString()
            Glide.with(context)
                .load(image)
                .placeholder(R.drawable.person_red)
                .error(R.drawable.person_red)
                .into(holder.img)
        }
    }

    override fun getItemCount(): Int {
        return researchersList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_name: TextView = itemView.findViewById(R.id.txt_name)
        val txt_position: TextView = itemView.findViewById(R.id.txt_position)
        val txt_address: TextView = itemView.findViewById(R.id.txt_address)
        val txt_contact: TextView = itemView.findViewById(R.id.txt_contact)
        val img: ImageView = itemView.findViewById(R.id.img)
    }
}