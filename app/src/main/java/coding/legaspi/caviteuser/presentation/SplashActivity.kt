package coding.legaspi.caviteuser.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.databinding.ActivitySplashBinding
import coding.legaspi.caviteuser.presentation.auth.LoginActivity
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.terms.TermsActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.SharedPreferences
import java.io.IOException
import javax.inject.Inject
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.presentation.auth.profilecreation.ProfileCreation


class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var loginViewModel: EventViewModel
    private val SPLASH_DELAY: Long = 1000
    private lateinit var binding: ActivitySplashBinding
    private lateinit var dialogHelper: DialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        loginViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)

        dialogHelper = DialogHelperFactory.create(this)

        val checkTerms = SharedPreferences().checkTerms(this)
        Log.i("Check Result", "Checking $checkTerms")
        Handler().postDelayed({
            if (checkTerms.isEmpty()){
                SharedPreferences().saveTerms(this, "true")
                checkLogin(checkTerms)
                Log.i("Check Result", "isEmpty ${checkTerms.isEmpty()}")
            }else{
                Log.i("Check Result", "else $checkTerms")
                checkLogin(checkTerms)
            }

        }, SPLASH_DELAY)
    }

    private fun checkLogin(checkTerms: String) {
        val (token, userid) = SharedPreferences().checkToken(this)
        val checkCreation = SharedPreferences().checkCreation(this)
        if (token != null && userid != null) {
            Log.i("Check Result", "Checking $token")
            try{
                Log.d("Check Result", "try")
                if (checkCreation == "true"){
                    val responseLiveData = loginViewModel.getUserId(userid.toString())
                    responseLiveData.observe(this, Observer {
                        Log.i("Check Result", "Checking $it")
                        when(it){
                            is Result.Success<*> -> {
                                // Handle success
                                val user = it.data as ProfileOutput
                                // Update UI with user data
                                Log.i("Check Result", "Success $user")
                                if (checkTerms == "true"){
                                    binding.progressBar.visibility = GONE
                                    val intent = Intent(this, TermsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    binding.progressBar.visibility = GONE
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            is Result.Error -> {
                                // Handle error
                                val exception = it.exception
                                // Show error message or handle error state
                                if (exception is IOException) {
                                    // Handle network failure
                                    Log.e("Check Result", "${exception.localizedMessage}")
                                    binding.progressBar.visibility = GONE
                                    if (exception.localizedMessage!! == "timeout"){
                                        dialogHelper.showUnauthorized(
                                            Error(
                                                "Server error",
                                                "Server is down or not reachable ${exception.message}"
                                            )
                                        )
                                    } else{
                                        // Handle other exceptions
                                        dialogHelper.showUnauthorized(
                                            Error(
                                                "Error",
                                                exception.localizedMessage!!
                                            )
                                        )
                                        Log.d("Check Result", "Unauthorized")
                                    }
                                } else {
                                    // Handle other exceptions
                                    binding.progressBar.visibility = GONE
                                    dialogHelper.showUnauthorized(
                                        Error(
                                            "Error",
                                            "Something went wrong!"
                                        )
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
                }else{
                    val intent = Intent(this, ProfileCreation::class.java)
                    intent.putExtra("userid", userid)
                    startActivity(intent)
                    finish()
                }
            }catch (e: Exception){
                Log.e("Check Result", "Exception $e")
            }

        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}