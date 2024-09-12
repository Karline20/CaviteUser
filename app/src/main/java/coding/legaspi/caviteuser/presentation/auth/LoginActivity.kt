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
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.auth.LoginBody
import coding.legaspi.caviteuser.data.model.auth.LoginBodyOutput
import coding.legaspi.caviteuser.databinding.ActivityLoginBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.VibrateView
import javax.inject.Inject
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.presentation.auth.profilecreation.ProfileCreation
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.SharedPreferences
import retrofit2.HttpException
import java.io.IOException
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
        if (validateUserCredentials()) {
            val responseLiveData = loginViewModel.getLoginEventUseCase(LoginBody(email, password))
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        val login = it.data as LoginBodyOutput
                        Log.d("aws", "Login 1 ${login.id}")
                        Log.d("aws", "emailVerified 1 ${login.emailVerified}")
                        Log.d("aws", "username 1 ${login.username}")
                        if (login.emailVerified) {
                            if (login.username == "user") {
                                checkProfile(login.id, login.token)
                                Log.d("aws", "Login 2 ${login.id}")
                            }
                        } else if (!login.emailVerified) {
                            loginBinding.progressBar.visibility = GONE
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Email verification",
                                    "Please verify your email first"
                                ),
                                positiveButtonFunction = {

                                }
                            )
                        }
                    }
                    is Result.Error -> {
                        // Handle error
                        val exception = it.exception
                        // Show error message or handle error state
                        if (exception is IOException) {
                            // Handle network failure
                            Log.e("Check Result", "showNetworkError")
                            Log.e("Check Result", "exception: $exception")
                            loginBinding.progressBar.visibility = GONE
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
                                loginBinding.progressBar.visibility = GONE
                                dialogHelper.showUnauthorized(
                                    Error(
                                        "Error",
                                        "Wrong credentials!"
                                    ),
                                    positiveButtonFunction = {

                                    }
                                )
                                Log.d("Check Result", "Unauthorized")
                            }

                        } else {
                            // Handle other exceptions
                            loginBinding.progressBar.visibility = GONE
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
                        loginBinding.progressBar.visibility = VISIBLE
                    }

                }
            })
        }
    }

    private fun checkProfile(userid: String, token: String) {
        try {
            val responseLiveData = loginViewModel.getByUserId(userid)
            responseLiveData.observe(this, Observer {
                when(it){
                    is Result.Success<*> -> {
                        val profile = it.data as ProfileOutput
                        Log.d("aws", "profile 1 $profile")
                        if (profile.id.isNotEmpty()){
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
                    }
                    is Result.Error -> {
                        // Handle error
                        val exception = it.exception
                        // Show error message or handle error state
                        if (exception is IOException) {
                            // Handle network failure
                            Log.e("Check Result", "showNetworkError")
                            loginBinding.progressBar.visibility = GONE
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
                                loginBinding.progressBar.visibility = GONE
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
                            loginBinding.progressBar.visibility = GONE
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
                        loginBinding.progressBar.visibility = VISIBLE
                    }

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