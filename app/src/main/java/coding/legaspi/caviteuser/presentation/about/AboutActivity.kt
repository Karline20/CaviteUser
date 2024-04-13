package coding.legaspi.caviteuser.presentation.about

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.researchers.Researchers
import coding.legaspi.caviteuser.data.model.researchers.ResearchersOutput
import coding.legaspi.caviteuser.databinding.ActivityAboutBinding
import coding.legaspi.caviteuser.presentation.about.adapter.AboutAdapter
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.menu.MenuActivity
import coding.legaspi.caviteuser.presentation.play.PlayActivity
import coding.legaspi.caviteuser.presentation.tutorial.TutorialActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.researcher
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import java.util.UUID
import javax.inject.Inject
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.DialogHelperImpl
import java.io.IOException

class AboutActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding : ActivityAboutBinding
    private lateinit var researchersOutput: ArrayList<ResearchersOutput>
    private lateinit var aboutAdapter: AboutAdapter
    private lateinit var dialogHelper: DialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        dialogHelper = DialogHelperFactory.create(this)
        (application as Injector).createEventsSubComponent().inject(this)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        researchersOutput = arrayListOf()
        aboutAdapter = AboutAdapter(researchersOutput,this )

        binding.loggedInTopNav.rrlFirst.visibility = GONE
        binding.loggedInTopNav.labelTitle.text = "About us"

        setContentView(view)
        setBottomButton()
        setProfile()
        setMenu()
        setAboutUs()
    }

    private fun setAboutUs() {
        FirebaseManager().fetchIdAboutFromFirebase {
            val responseLiveData = eventViewModel.getAboutUs(it)
            responseLiveData.observe(this, Observer{
                if (it!=null){
                    val description = it.body()?.description
                    binding.about2.text = description
                    setRvResearcher()
                }
            })
        }

    }

    private fun setRvResearcher() {
        val responseLiveData = eventViewModel.getReserachers()
        responseLiveData.observe(this, Observer{
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as List<ResearchersOutput>
                    if (result!=null){
                        if (result.isEmpty()){
                            binding.noData.visibility = VISIBLE
                            binding.rvResearcher.visibility = GONE
                        }else{
                            researchersOutput.clear()
                            researchersOutput.addAll(result)
                            val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                            binding.rvResearcher.layoutManager = llm
                            binding.rvResearcher.adapter = aboutAdapter
                            binding.noData.visibility=GONE
                            aboutAdapter.notifyDataSetChanged()
                        }
                    }
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
                                    recreate()
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
    }

    private fun setMenu() {
        binding.loggedInTopNav.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
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
                        .into(binding.loggedInTopNav.imgProfile)
                }
            }
        }
    }

    private fun setBottomButton() {
        binding.loggedInBottomNav.info.setImageResource(R.drawable.info_red)

        binding.loggedInBottomNav.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.tutorial.setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.play.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.info.setOnClickListener {

        }
    }
}