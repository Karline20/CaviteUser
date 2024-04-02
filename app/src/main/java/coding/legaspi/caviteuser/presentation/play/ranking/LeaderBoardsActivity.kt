package coding.legaspi.caviteuser.presentation.play.ranking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.ranking.RankingOutput
import coding.legaspi.caviteuser.databinding.ActivityLeaderBoardsBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.play.adapter.LeaderBoardsAdapter
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

class LeaderBoardsActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var eventViewModel: EventViewModel
    private lateinit var dialogHelper: DialogHelper
    private lateinit var binding: ActivityLeaderBoardsBinding
    private lateinit var rankingList: ArrayList<RankingOutput>
    private lateinit var leaderBoardsAdapter: LeaderBoardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderBoardsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent()
            .inject(this)
        eventViewModel= ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        rankingList = arrayListOf()
        leaderBoardsAdapter = LeaderBoardsAdapter(rankingList, this)
        binding.progressBar.visibility = VISIBLE

        setButton()
        setProfile()
        setRv()
    }

    private fun setRv() {
        val responseLiveData = eventViewModel.getTopLeaderBoards()
        responseLiveData.observe(this, Observer {
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as List<RankingOutput>
                    if (result!=null){
                        rankingList.clear()
                        rankingList.addAll(result)
                        val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        binding.rvRank.layoutManager = llm
                        binding.rvRank.adapter = leaderBoardsAdapter
                        binding.progressBar.visibility = GONE
                        binding.noData.visibility= GONE
                        leaderBoardsAdapter.notifyDataSetChanged()
                    }else{
                        binding.noData.visibility= VISIBLE
                        binding.rvRank.visibility= GONE
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
        val (token, userid) = SharedPreferences().checkToken(this)
        if (userid != null){
            FirebaseManager().fetchProfileFromFirebase(userid){
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

    private fun setButton() {
        binding.loggedInTopNav.back.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}