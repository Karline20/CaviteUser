package coding.legaspi.caviteuser.presentation.auth


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.auth.LoginBody
import coding.legaspi.caviteuser.databinding.ActivityLoginBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.VibrateView
import javax.inject.Inject
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.presentation.auth.profilecreation.ProfileCreation
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.SharedPreferences
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var loginViewModel: EventViewModel
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var dialogHelper: DialogHelper
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        loginViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)

        loginBinding.progressBar.visibility = GONE
        loginBinding.loginBtn.setOnClickListener(this)
        loginBinding.txtRegister.setOnClickListener(this)
        loginBinding.etEmail.onFocusChangeListener = this
        loginBinding.etPassword.onFocusChangeListener = this
        loginBinding.etPassword.setOnKeyListener(this)

    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.loginBtn -> {
                    submitForm()
                }
                R.id.txt_register -> {
                    val intent = Intent(this, RegistrationActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun submitForm() {
        loginBinding.progressBar.visibility = VISIBLE
        if (validateUserCredentials()){
            val responseLiveData = loginViewModel.getLoginEventUseCase(LoginBody(email, password))
            responseLiveData.observe(this, Observer {
                try {
                    Log.d("aws", "Login 1 ${it.body()?.id.toString()}")
                    if (it!=null){
                        val response = it.body()
                        if (response?.emailVerified == true){
                            if (response.username == "user"){
                                checkProfile(response.id, response.token)
                                Log.d("Login", "Login 1 ${it.body()?.id.toString()}")
                            }
                        }else if(response?.emailVerified == false){
                            loginBinding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(Error("Email verification", "Please verify your email first"))
                        }else{
                            loginBinding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(Error(it.code().toString(), it.message().toString()))
                        }
                    }else{
                        loginBinding.progressBar.visibility = GONE
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        dialogHelper.showUnauthorized(Error(it?.code().toString(), it?.message().toString()))
                    }
                }catch (httpException: HttpException) {
                    Log.e("LoginActivity", "HTTP Exception: ${httpException.code()}, ${httpException.message()}")
                    dialogHelper.showUnauthorized(Error("Connection error", "$httpException"))
                    loginBinding.progressBar.visibility = GONE
                } catch (socketTimeoutException: SocketTimeoutException) {
                    Log.e("LoginActivity", "SocketTimeoutException: $socketTimeoutException")
                    loginBinding.progressBar.visibility = GONE
                    dialogHelper.showUnauthorized(Error("Connection error", "Can't connect to the server"))
                }
            })
        }
    }

    private fun checkProfile(userid: String, token: String) {
        try {
            val responseLiveData = loginViewModel.getByUserId(userid)
            responseLiveData.observe(this, Observer {
                if (it != null && it.body()?.id != null){
                    SharedPreferences().saveToken(this, token, userid)
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("userid", userid)
                    startActivity(intent)
                    loginBinding.progressBar.visibility = GONE
                    finish()
                }else{
                    Log.d("ProfileCreation", "$it")
                    SharedPreferences().saveToken(this, token, userid)
                    val intent = Intent(this, ProfileCreation::class.java)
                    intent.putExtra("userid", userid)
                    startActivity(intent)
                    loginBinding.progressBar.visibility = GONE
                    finish()
                }
            })
        }catch (httpException: HttpException){
            Log.e("LoginActivity", "Error 1: $httpException")
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.etEmail -> {
                    if (hasFocus) {
                        if (loginBinding.etEmailTil.isCounterEnabled) {
                            loginBinding.etEmailTil.isCounterEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }

                R.id.etPassword -> {
                    if (hasFocus) {
                        if (loginBinding.etPasswordTil.isCounterEnabled) {
                            loginBinding.etPasswordTil.isCounterEnabled = false
                        }
                    } else {
                        validatePassword()
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        if(event == KeyEvent.KEYCODE_ENTER && keyEvent!!.action == KeyEvent.ACTION_UP){
            submitForm()
        }
        return false
    }

    private fun validateUserCredentials(): Boolean{
        var isValid = true
        if (!validateEmail(shouldVibrate = false)) isValid = false
        if (!validatePassword(shouldVibrate = false)) isValid = false
        if (!isValid) VibrateView.vibrate(this, loginBinding.rrlLogin)
        return isValid
    }

    private fun validateEmail(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        email = loginBinding.etEmail.text.toString()
        if (email.isEmpty()) {
            errorMessage = "Email is required!"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Invalid Email!"
        }
        if (errorMessage != null && shouldUpdateView) {
            loginBinding.etEmailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@LoginActivity, this)
            }
        }
        return errorMessage == null
    }

    private fun validatePassword(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        password = loginBinding.etPassword.text.toString()

        if (password.isEmpty()) {
            errorMessage = "Password is required!"
        } else if (password.length < 8) {
            errorMessage = "Password must be 8 character long!"
        }
        if (errorMessage != null && shouldUpdateView) {
            loginBinding.etPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@LoginActivity, this)
            }
        }
        return errorMessage == null
    }

}