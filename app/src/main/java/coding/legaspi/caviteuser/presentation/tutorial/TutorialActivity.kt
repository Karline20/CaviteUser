package coding.legaspi.caviteuser.presentation.tutorial

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.tutorial.Tutorial
import coding.legaspi.caviteuser.databinding.ActivityTutorialBinding
import coding.legaspi.caviteuser.presentation.about.AboutActivity
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.menu.MenuActivity
import coding.legaspi.caviteuser.presentation.play.PlayActivity
import coding.legaspi.caviteuser.presentation.tutorial.adapter.TutorialAdapter
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.SharedPreferences
import com.bumptech.glide.Glide
import javax.inject.Inject
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.error.Error
import java.io.IOException

class TutorialActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var dialogHelper: DialogHelper
    private lateinit var binding: ActivityTutorialBinding
    private lateinit var tutorialAdapter: TutorialAdapter
    private lateinit var tutorialList: ArrayList<Tutorial>

    companion object{
        val TutorialActivity = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        tutorialList = arrayListOf()
        tutorialAdapter = TutorialAdapter(tutorialList, this@TutorialActivity, this, eventViewModel)
        binding.progressBar.visibility = VISIBLE
        setBottomButton()
        setProfile()
        setRv()
        setMenu()
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = VISIBLE
        setProfile()
        setRv()
    }

    override fun onStart() {
        super.onStart()
        binding.progressBar.visibility = VISIBLE
        setProfile()
        setRv()
    }

    private fun setRv() {
        val responseLiveData = eventViewModel.getTutorial()
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as List<Tutorial>
                    if (result!=null){
                        if (result.isNullOrEmpty()){
                            binding.noData.visibility= VISIBLE
                            binding.rvTutorial.visibility= GONE
                            binding.progressBar.visibility = GONE
                        }else{
                            tutorialList.clear()
                            tutorialList.addAll(result)
                            val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                            binding.rvTutorial.layoutManager = llm
                            binding.rvTutorial.adapter = tutorialAdapter
                            binding.progressBar.visibility = GONE
                            tutorialAdapter.notifyDataSetChanged()
                        }
                    }else{

                    }
                }
                is Result.Error -> {
                    val exception = it.exception

                    if (exception is IOException) {
                        binding.progressBar.visibility = GONE
                        if (exception.localizedMessage!! == "timeout"){
                            dialogHelper.showUnauthorized(
                                Error(
                                    "Server error",
                                    "Server is down or not reachable ${exception.message}"
                                )
                            )
                        } else{
                            dialogHelper.showUnauthorized(Error("Error",exception.localizedMessage!!))
                        }
                    } else {
                        binding.progressBar.visibility = GONE
                        dialogHelper.showUnauthorized(Error("Error","Something went wrong!"))
                    }
                }
                Result.Loading -> {
                    binding.progressBar.visibility = VISIBLE
                }
            }
        })
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
        binding.loggedInBottomNav.tutorial.setImageResource(R.drawable.baseline_record_voice_over_24)

        binding.loggedInBottomNav.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.tutorial.setOnClickListener {

        }
        binding.loggedInBottomNav.play.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.loggedInBottomNav.info.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setMenu() {
        binding.loggedInTopNav.menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}