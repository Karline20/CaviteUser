package coding.legaspi.caviteuser.presentation.terms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.databinding.ActivityTermsBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import javax.inject.Inject

class TermsActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var termsBinding: ActivityTermsBinding
    private lateinit var dialogHelper: DialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        termsBinding = ActivityTermsBinding.inflate(layoutInflater)
        val view = termsBinding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)

        setTerms()
        setButton()
    }

    private fun setButton() {
        termsBinding.verifyBtn.setOnClickListener {
            isChecked()
        }
    }

    private fun isChecked() {
        if (termsBinding.cb1.isChecked && termsBinding.cb2.isChecked){
            SharedPreferences().saveTerms(this, "false")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            dialogHelper.showError(Error("Read and verify", "Please read first and verify the terms and condition"))
        }
    }

    private fun setTerms() {
        FirebaseManager().fetchIdTermsFromFirebase {
            Log.d("Terms", it)
            if (it!=null){
                val id = it
                val responseLiveData = eventViewModel.getTerms(id)
                responseLiveData.observe(this, Observer {
                    if (it!=null){
                        val firstTitle = it.body()?.ftitle
                        val firstDesc = it.body()?.fdesc
                        val secTitle = it.body()?.stitle
                        val secDesc = it.body()?.sdesc
                        termsBinding.tt1.text = firstTitle
                        termsBinding.tt2.text = secTitle
                        termsBinding.desc1.text = firstDesc
                        termsBinding.desc2.text = secDesc
                    }
                })
            }
        }

    }
}