package coding.legaspi.caviteuser.presentation.play.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.ranking.RankingOutput


class LeaderBoardsAdapter(
    private var rankingList: ArrayList<RankingOutput>,
    private val context: Context
): RecyclerView.Adapter<LeaderBoardsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.rv_rank, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = rankingList[position].id
        val userid = rankingList[position].userid
        val name = rankingList[position].name
        val date = rankingList[position].date
        val timestamp = rankingList[position].timestamp
        val score = rankingList[position].score

        val increment = 1
        val exactPos = increment+position
        Log.d("Position", "$exactPos")
        when(exactPos){
            1-> {
                holder.img_rank.setColorFilter(ContextCompat.getColor(context, R.color.gold))
            }
            2-> {
                holder.img_rank.setColorFilter(ContextCompat.getColor(context, R.color.silver))
            }
            3-> {
                holder.img_rank.setColorFilter(ContextCompat.getColor(context, R.color.bronze))
            }
            else-> holder.img_rank.setColorFilter(ContextCompat.getColor(context, R.color.owtoRed))
        }
        holder.label_rank.text = "$exactPos"
        holder.label_score.text = "Score: $score"
        holder.label_name.text = name
        holder.label_date.text = date
    }

    override fun getItemCount(): Int {
        return rankingList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_rank: ImageView = itemView.findViewById(R.id.img_rank)
        val label_name: TextView = itemView.findViewById(R.id.label_name)
        val label_score: TextView = itemView.findViewById(R.id.label_score)
        val label_date: TextView = itemView.findViewById(R.id.label_date)
        val label_rank: TextView = itemView.findViewById(R.id.label_rank)
    }
}