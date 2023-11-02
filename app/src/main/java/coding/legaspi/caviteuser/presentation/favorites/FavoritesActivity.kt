package coding.legaspi.caviteuser.presentation.favorites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.favorites.FavoritesOutput
import coding.legaspi.caviteuser.databinding.ActivityFavoritesBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.favorites.adapter.FavoritesAdapter
import coding.legaspi.caviteuser.presentation.menu.MenuActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import javax.inject.Inject

class FavoritesActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var dialogHelper: DialogHelper
    private lateinit var favoritesBinding: ActivityFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoritesList: ArrayList<FavoritesOutput>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoritesBinding = ActivityFavoritesBinding.inflate(layoutInflater)
        val view = favoritesBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        val (token, userId) = SharedPreferences().checkToken(this)
        favoritesBinding.progressBar.visibility = VISIBLE
        favoritesList = arrayListOf()
        favoritesAdapter = FavoritesAdapter(favoritesList, this, eventViewModel, this)
        setMenu()
        setRv(userId)
        setProfile(userId)
    }

    private fun setProfile(userId: String?) {
        if (userId != null){
            FirebaseManager().fetchProfileFromFirebase(userId){
                if (it!=null){
                    Glide.with(this)
                        .load(it.imageUri)
                        .placeholder(R.drawable.baseline_broken_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(favoritesBinding.loggedInTopNav.imgProfile)
                }
            }
        }
    }

    private fun setRv(userId: String?) {
        val responseLiveData = eventViewModel.getFavorites(userId.toString())
        responseLiveData.observe(this, Observer {
            if (it!=null){
                if (it.isEmpty()){
                    favoritesBinding.rvFavorites.visibility = GONE
                    favoritesBinding.noData.visibility = VISIBLE
                    favoritesBinding.progressBar.visibility = GONE
                }else{
                    favoritesList.clear()
                    favoritesList.addAll(it)
                    val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    favoritesBinding.rvFavorites.layoutManager = llm
                    favoritesBinding.rvFavorites.adapter = favoritesAdapter
                    favoritesBinding.progressBar.visibility = GONE
                    favoritesBinding.noData.visibility = GONE
                }
            }else{
                favoritesBinding.rvFavorites.visibility = GONE
                favoritesBinding.noData.visibility = VISIBLE
            }
        })
    }

    private fun setMenu() {
        favoritesBinding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}