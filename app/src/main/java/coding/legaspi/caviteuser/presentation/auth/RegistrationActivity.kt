package coding.legaspi.caviteuser.presentation.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import coding.legaspi.caviteuser.data.model.auth.SignBody
import coding.legaspi.caviteuser.data.model.auth.SignBodyOutput
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.databinding.ActivityRegistrationBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.terms.TermsActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import coding.legaspi.caviteuser.utils.VibrateView
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var loginViewModel: EventViewModel
    private lateinit var registrationBinding: ActivityRegistrationBinding
    private lateinit var dialogHelper: DialogHelper
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var cpassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = registrationBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        loginViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)

        registrationBinding.progressBar.visibility = VISIBLE
        registrationBinding.regBtn.setOnClickListener(this)
        registrationBinding.txtLogin.setOnClickListener(this)
        registrationBinding.etEmail.onFocusChangeListener = this
        registrationBinding.etPassword.onFocusChangeListener = this
        registrationBinding.etCPassword.onFocusChangeListener = this
        registrationBinding.etPassword.setOnKeyListener(this)

    }

    override fun onResume() {
        super.onResume()
        registrationBinding.progressBar.visibility = VISIBLE
        check()

    }

    private fun check() {
        FirebaseManager().checkUser(this) {
            if (it){
                Log.d("RegistrationActivity", "onResume")
                val responseLiveData = loginViewModel.getLoginEventUseCase(SignBody(email, password, "user", true ))
                responseLiveData.observe(this, Observer {
                    when(it){
                        is Result.Success<*> -> {
                            val signup = it.data as SignBodyOutput
                            FirebaseManager().logout{
                                if (it){
                                    registrationBinding.progressBar.visibility = GONE
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        is Result.Error -> {
                            val exception = it.exception
                            if (exception is IOException) {
                                Log.e("Check Result", "${exception.localizedMessage}")
                                registrationBinding.progressBar.visibility = GONE
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
                                registrationBinding.progressBar.visibility = GONE
                                dialogHelper.showUnauthorized(
                                    Error(
                                        "Error",
                                        "Something went wrong!"
                                    ),
                                    positiveButtonFunction = {

                                    }
                                )
                            }
                        }
                        Result.Loading -> {
                            registrationBinding.progressBar.visibility = VISIBLE
                        }
                    }
                })
            }else{
                registrationBinding.progressBar.visibility = GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        check()
    }
    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.regBtn -> {
                    submitForm()
                }
                R.id.txt_login ->{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun submitForm() {
        if (validateUserCredentials()){
            registrationBinding.progressBar.visibility = VISIBLE
            FirebaseManager().registerUser(email, password){
                if (it){
                    registrationBinding.progressBar.visibility = GONE
                    //dialogHelper.showSuccess("Email verification!", "Email sent...")
                    SharedPreferences().saveCreation(this, "false")
                    dialogHelper.showEmailVerification("Email Verification", "Please check your email.")
                }else{
                    registrationBinding.progressBar.visibility = GONE
                    dialogHelper.showUnauthorized(Error("Invalid!", "Email is already taken..."),
                        positiveButtonFunction = {

                        })
                }
            }
        }
    }
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.etEmail -> {
                    if (hasFocus) {
                        if (registrationBinding.etEmailTil.isCounterEnabled) {
                            registrationBinding.etEmailTil.isCounterEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }

                R.id.etPassword -> {
                    if (hasFocus) {
                        if (registrationBinding.etPasswordTil.isCounterEnabled) {
                            registrationBinding.etPasswordTil.isCounterEnabled = false
                        }
                    } else {
                        if (validatePassword() && registrationBinding.etCPassword.text!!.isNotEmpty() &&
                            validateConfirmPassword()
                        ) {
                            if (registrationBinding.etCPasswordTil.isErrorEnabled) {
                                registrationBinding.etCPasswordTil.isCounterEnabled = false
                            }
                            registrationBinding.etCPasswordTil.setStartIconDrawable(R.drawable.baseline_check_circle_24)
                        }
                    }
                }

                R.id.etCPassword -> {
                    if (hasFocus){
                        if(registrationBinding.etCPasswordTil.isCounterEnabled){
                            registrationBinding.etCPasswordTil.isCounterEnabled = false
                        }
                    }else{
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()) {
                            if (registrationBinding.etPasswordTil.isErrorEnabled) {
                                registrationBinding.etPasswordTil.isErrorEnabled = false
                            }
                            registrationBinding.etCPasswordTil.apply {
                                setStartIconDrawable(R.drawable.baseline_check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
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
        if (!validateConfirmPassword(shouldVibrate = false)) isValid = false
        if (isValid && !validateConfirmPassword(shouldVibrate = false)) isValid = false

        if (!isValid) VibrateView.vibrate(this, registrationBinding.rrlLogin)

        return isValid
    }

    private fun validateEmail(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        email = registrationBinding.etEmail.text.toString()
        if (email.isEmpty()) {
            errorMessage = "Email is required!"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Invalid Email!"
        }
        if (errorMessage != null && shouldUpdateView) {
            registrationBinding.etEmailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@RegistrationActivity, this)
            }
        }
        return errorMessage == null
    }

    private fun validatePassword(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        password = registrationBinding.etPassword.text.toString()

        if (password.isEmpty()) {
            errorMessage = "Password is required!"
        } else if (password.length < 8) {
            errorMessage = "Password must be 8 character long!"
        }
        if (errorMessage != null && shouldUpdateView) {
            registrationBinding.etPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@RegistrationActivity, this)
            }
        }
        return errorMessage == null
    }

    private fun validateConfirmPassword(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        cpassword = registrationBinding.etCPassword.text.toString()
        if (cpassword.isEmpty()) {
            errorMessage = "Confirm Password is required!"
        } else if (cpassword.length < 8) {
            errorMessage = "Password must be 8 character long!"
        }
        if (errorMessage != null && shouldUpdateView) {
            registrationBinding.etCPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@RegistrationActivity, this)
            }
        }
        return errorMessage == null
    }

    private fun validatePasswordAndConfirmPassword(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        password = registrationBinding.etPassword.text.toString()
        cpassword = registrationBinding.etCPassword.text.toString()
        if (password != cpassword) {
            errorMessage = "Password doesn't match!"
        }
        if (errorMessage != null && shouldUpdateView) {
            registrationBinding.etPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@RegistrationActivity, this)
            }
        }

        return errorMessage == null
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (validatePassword(shouldUpdateView = false) && validateConfirmPassword(shouldUpdateView = false) &&
            validatePasswordAndConfirmPassword(shouldUpdateView = false)){
            registrationBinding.etCPasswordTil.apply {
                if (isErrorEnabled) isErrorEnabled = false
                setStartIconDrawable(R.drawable.baseline_check_circle_24)
                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
            }
        }else{
            if (registrationBinding.etCPasswordTil.startIconDrawable != null)
                registrationBinding.etCPasswordTil.startIconDrawable = null
        }


    }

    override fun afterTextChanged(s: Editable?) {
    }

}