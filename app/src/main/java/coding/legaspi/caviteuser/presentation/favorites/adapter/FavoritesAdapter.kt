package coding.legaspi.caviteuser.presentation.favorites.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.adaptermodel.Image
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput
import coding.legaspi.caviteuser.data.model.favorites.FavoritesOutput
import coding.legaspi.caviteuser.presentation.home.event.ViewEventActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.ImageAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException

class FavoritesAdapter(
    private val favoritesList: ArrayList<FavoritesOutput>,
    private val context: Context,
    private val eventViewModel: EventViewModel,
    private val lifecycle: LifecycleOwner
): RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    private lateinit var imageList: ArrayList<Image>
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialogHelper: DialogHelper

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val img_unfave: ImageView = itemView.findViewById(R.id.img_unfave)
        val tv_desc: TextView = itemView.findViewById(R.id.tv_desc)
        val tv_title: TextView = itemView.findViewById(R.id.tv_title)
        val epoxy_image: RecyclerView = itemView.findViewById(R.id.epoxy_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_favorites, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dialogHelper = DialogHelperFactory.create(context)
        val id = favoritesList[position].id
        val userid = favoritesList[position].userid
        val eventid = favoritesList[position].eventid
        val date = favoritesList[position].date
        val timestamp = favoritesList[position].timestamp

        setEvent(eventid, holder)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewEventActivity::class.java)
            intent.putExtra("id", eventid)
            context.startActivity(intent)
        }
        holder.img_unfave.setOnClickListener {
            dialogHelper.delete("Remove Favorite", "Do you want to remove this from favorites", "Yes", "Cancel"){
                if (it){
                    removeFave(id)
                }
            }
        }
    }

    private fun removeFave(id: String) {
        val responseLiveData = eventViewModel.delFavorites(id)
        responseLiveData.observe(lifecycle, Observer {
            when(it) {
                is Result.Success<*> -> {
                    dialogHelper.showError(Error("Remove", "Removed successfully"))
                }
                is Result.Error -> {
                    val exception = it.exception
                    if (exception is IOException) {
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                ),
                                positiveButtonFunction = {

                                }
                            )
                        } else{
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Error",
                                    exception.localizedMessage!!
                                ),
                                positiveButtonFunction = {

                                }
                            )
                        }
                    } else {
                        dialogHelper.showUnauthorized(
                            Error(
                                "Error",
                                "Something went wrong!"
                            ),
                            positiveButtonFunction = {

                            }
                        )
                        Log.d("Check Result", "showGenericError")
                    }
                }
                Result.Loading -> {
                    // Handle loading state
                    Log.d("Check Result", "Loading")
                }
            }

        })
    }

    private fun setEvent(eventid: String, holder: ViewHolder) {
        val responseLiveData = eventViewModel.getEventsById(eventid)
        responseLiveData.observe(lifecycle, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as AllModelOutput
                    holder.tv_title.text = result.name
                    holder.tv_desc.text = result.description
                }
                is Result.Error -> {
                    val exception = it.exception

                    if (exception is IOException) {
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                ),
                                positiveButtonFunction = {

                                }
                            )
                        } else{
                            dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!),
                                positiveButtonFunction = {

                                })
                        }
                    } else {
                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                            positiveButtonFunction = {

                            })
                    }
                }
                Result.Loading -> {

                }
            }
        })
        imageList = arrayListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("Images")
        databaseReference.child(eventid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        imageList.clear()
                        for (snapshot in snapshot.children) {
                            val image = snapshot.getValue(Image::class.java)
                            if (image != null) {
                                imageList.add(image)
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
                    Log.e("EventAdapter", "rv Image $error")
                }
            })
    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }
}