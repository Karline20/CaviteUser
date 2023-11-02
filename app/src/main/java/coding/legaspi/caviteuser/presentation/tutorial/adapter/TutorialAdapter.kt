package coding.legaspi.caviteuser.presentation.tutorial.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.tutorial.Tutorial
import coding.legaspi.caviteuser.presentation.tutorial.tutor.TutorActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.SharedPreferences

class TutorialAdapter(
    private var tutorialList: ArrayList<Tutorial>,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val eventViewModel: EventViewModel,
): RecyclerView.Adapter<TutorialAdapter.ViewHolder>() {

    private var dialogHelper: DialogHelper = DialogHelperFactory.create(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_tutorial, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = tutorialList[position].id
        val tutorial = tutorialList[position].tutorial

        val increment = 1
        val exactPos = increment+position
        var isFinished = false
        val (token, userid) = SharedPreferences().checkToken(context)
        val responseLiveData = eventViewModel.getTutorialByUserId(id, userid.toString())
        responseLiveData.observe(lifecycleOwner, Observer {
            if (it!=null) {
                val isFinish = it.body()?.isFinish
                if (isFinish == true){
                    isFinished = true
                holder.imageIsFinish.setImageResource(R.drawable.baseline_check_circle_24)
                }else{
                    holder.imageIsFinish.setImageResource(R.drawable.baseline_do_not_disturb_on_24)
                }
            }else{
                holder.imageIsFinish.setImageResource(R.drawable.baseline_do_not_disturb_on_24)
            }
        })

        holder.count.text = "Tutorial $exactPos"
        holder.textView.text = tutorial

        holder.itemView.setOnClickListener {
            if (isFinished){
                dialogHelper.showSuccess(tutorial, "Thank you but you are already finish with this tutorial!")
            }else{
                val tutorialExactPos = holder.count.text
                val intent = Intent(context, TutorActivity::class.java)
                intent.putExtra("tutorialid", id)
                intent.putExtra("userid", userid)
                intent.putExtra("tutorial", tutorial)
                intent.putExtra("tutorialExactPos", tutorialExactPos)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return tutorialList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val count: TextView = itemView.findViewById(R.id.count)
        val imageIsFinish: ImageView = itemView.findViewById(R.id.imageIsFinish)

    }
}