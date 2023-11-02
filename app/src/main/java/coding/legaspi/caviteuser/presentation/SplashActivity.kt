package coding.legaspi.caviteuser.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.databinding.ActivitySplashBinding
import coding.legaspi.caviteuser.presentation.auth.LoginActivity
import coding.legaspi.caviteuser.presentation.auth.profilecreation.ProfileCreation
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.terms.TermsActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.SharedPreferences
import javax.inject.Inject


class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var loginViewModel: EventViewModel
    private val SPLASH_DELAY: Long = 1000
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        loginViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        val checkTerms = SharedPreferences().checkTerms(this)
        Handler().postDelayed({
            if (checkTerms.isEmpty()){
                SharedPreferences().saveTerms(this, "true")
                checkLogin(checkTerms)
            }else{
                checkLogin(checkTerms)
            }

        }, SPLASH_DELAY)
    }

    private fun checkLogin(checkTerms: String) {
        val (token, userid) = SharedPreferences().checkToken(this)
        if (token != null) {
            val responseLiveData = loginViewModel.getUserId(userid.toString())
            responseLiveData.observe(this, Observer {
                if (it != null && it.body()?.id != null) {
                    if (checkTerms.equals("true")){
                        val intent = Intent(this, TermsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this, ProfileCreation::class.java)
                    intent.putExtra("userid", userid)
                    startActivity(intent)
                    finish()
                }
            })
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}