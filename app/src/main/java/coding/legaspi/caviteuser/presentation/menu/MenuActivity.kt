package coding.legaspi.caviteuser.presentation.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.databinding.ActivityMenuBinding
import coding.legaspi.caviteuser.presentation.about.AboutActivity
import coding.legaspi.caviteuser.presentation.auth.LoginActivity
import coding.legaspi.caviteuser.presentation.auth.profilecreation.ProfileCreation
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.favorites.FavoritesActivity
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.play.PlayActivity
import coding.legaspi.caviteuser.presentation.tutorial.TutorialActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.io.Serializable
import javax.inject.Inject

class MenuActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var loginViewModel: EventViewModel
    private lateinit var binding: ActivityMenuBinding
    private lateinit var dialogHelper: DialogHelper

    lateinit var profileObject: ProfileOutput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        loginViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        setProfile()
        setMenuButton()
    }

    private fun setProfile() {
        val (token, userId) = SharedPreferences().checkToken(this)
        if (userId != null){
            FirebaseManager().fetchProfileFromFirebase(userId){
                if (it!=null){
                    Glide.with(this)
                        .load(it.imageUri)
                        .placeholder(R.drawable.baseline_broken_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(binding.imgProfile)
                }
            }

            val responseLiveData = loginViewModel.getByUserId(userId)
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        val profile = it.data as ProfileOutput
                        if (profile!=null){
                            val firstname = profile.firstname
                            val lastname = profile.lastname

                            profileObject = profile
                            binding.txtName.text = "$firstname $lastname"
                        }

                    }
                    is Result.Error -> {
                        // Handle error
                        val exception = it.exception
                        // Show error message or handle error state
                        if (exception is IOException) {
                            // Handle network failure
                            Log.e("Check Result", "showNetworkError")
                            binding.progressBar.visibility = GONE
                            if (exception.equals("java.net.SocketTimeoutException")){
                                dialogHelper.showUnauthorized(
                                    Error(
                                        "Server error",
                                        "Server is down or not reachable ${exception.localizedMessage}"
                                    ),
                                    positiveButtonFunction = {
                                        recreate()
                                    }
                                )
                            } else{
                                // Handle other exceptions
                                binding.progressBar.visibility = GONE
                                dialogHelper.showUnauthorized(
                                    Error(
                                        "Error",
                                        exception.localizedMessage!!
                                    ),
                                    positiveButtonFunction = {
                                    }
                                )
                                Log.d("Check Result", "Unauthorized")
                            }

                        } else {
                            // Handle other exceptions
                            binding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Error",
                                    "$exception"
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
                        binding.progressBar.visibility = VISIBLE
                    }

                }
            })
        }
    }

    private fun setMenuButton() {
        val (token, userid) = SharedPreferences().checkToken(this)
        binding.updateProfile.setOnClickListener{
            val intent = Intent(this, ProfileCreation::class.java)
            intent.putExtra("userid", userid)
            intent.putExtra("one", "one")
            startActivity(intent)
        }
        binding.back.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.favorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.tutorial.setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.play.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.logout.setOnClickListener {
            try {
                dialogHelper.showLogout("Logout", "Are you sure you want to logout?", "Yes", "Cancel"){
                    if (it){
                        binding.progressBar.visibility=VISIBLE
                        Log.d("Menu", "$it")
                        SharedPreferences().deleteToken(this)
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        binding.progressBar.visibility=GONE
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Can't logout...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}