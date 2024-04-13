package coding.legaspi.caviteuser.presentation.favorites

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
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import javax.inject.Inject
import coding.legaspi.caviteuser.Result
import java.io.IOException
import coding.legaspi.caviteuser.data.model.error.Error

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
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as List<FavoritesOutput>
                    if (result!=null){
                        if (result.isEmpty()){
                            favoritesBinding.rvFavorites.visibility = GONE
                            favoritesBinding.noData.visibility = VISIBLE
                            favoritesBinding.progressBar.visibility = GONE
                        }else{
                            favoritesList.clear()
                            favoritesList.addAll(result)
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
                }
                is Result.Error -> {
                    val exception = it.exception

                    if (exception is IOException) {
                        favoritesBinding.progressBar.visibility = GONE
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                ),
                                positiveButtonFunction = {
                                    recreate()
                                }
                            )
                        } else{
                            dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!),
                                positiveButtonFunction = {

                                })
                        }
                    } else {
                        favoritesBinding.progressBar.visibility = GONE
                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"),
                            positiveButtonFunction = {

                            })
                    }
                }
                Result.Loading -> {
                    favoritesBinding.progressBar.visibility = VISIBLE
                }
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