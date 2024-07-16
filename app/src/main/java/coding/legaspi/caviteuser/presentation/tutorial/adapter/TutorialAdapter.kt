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
import coding.legaspi.caviteuser.Result
import coding.legaspi.caviteuser.data.model.tutorial.TutorialStatusOutput
import java.io.IOException
import coding.legaspi.caviteuser.data.model.error.Error
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput

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
            when(it){
                is Result.Success<*> -> {
                    val result = it.data as TutorialStatusOutput
                    if (result!=null) {
                        val isFinish = result.isFinish
                        if (isFinish){
                            isFinished = true
                            holder.imageIsFinish.setImageResource(R.drawable.baseline_check_circle_24)
                        }else{
                            holder.imageIsFinish.setImageResource(R.drawable.baseline_do_not_disturb_on_24)
                        }
                    }else{
                        holder.imageIsFinish.setImageResource(R.drawable.baseline_do_not_disturb_on_24)
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
        when(tutorial) {
            "MAGANDA KA - BONITA USTE" -> holder.textView.text =  "MAGANDA KA - BO-NI-TA US-TE"
            "MAHAL KITA - TA AMA YO CONTIGO" -> holder.textView.text = "MAHAL KITA - TA A-MA YO CON-TI-GO"
            "AYOS LANG - MUY BIEN" -> holder.textView.text = "AYOS LANG - MUY BI-EN"
            "MAGANDANG HAPON - BUENAS TARDES" -> holder.textView.text = "MAGANDANG HAPON - BUE-NAS TAR-DES"
            "KAMUSTA KA - QUETAL MAN USTE" -> holder.textView.text = "KAMUSTA KA - QUE-TAL MAN US-TE"
            "SALAMAT - GRACIAS" -> holder.textView.text = "SALAMAT - GRA-CIAS"
            "PAALAM - ADIOS" -> holder.textView.text = "PAALAM - AD-IOS"
            "PASINTABI - CON PERMISO"-> holder.textView.text = "PASINTABI - CON PER-MI-SO"
            "TULOY KAYO - BIENVENIDOS"-> holder.textView.text = "TULOY KAYO - BI-EN-VE-NI-DOS"
            "MAGANDANG GABI - BUENAS NOCHES"-> holder.textView.text = "MAGANDANG GABI - BUE-NAS NO-CHES"
            "PATAWAD - PERDONA CONMIGO"-> holder.textView.text = "PATAWAD - PER-DO-NA CON-MI-GO"
            "MAGANDANG UMAGA - BUENAS DIAS"-> holder.textView.text = "MAGANDANG UMAGA - BUE-NAS DI-AS"
            "INGAT KA - QUIDAO"-> holder.textView.text = "INGAT KA - QUI-DAO"
            "AMA - PADRE"-> holder.textView.text = "AMA - PAD-RE"
            "ANAK NA BABAE - HIJA"-> holder.textView.text = "ANAK NA BABAE - HI-JA"
            "ANAK NA LALAKI - HIJO"-> holder.textView.text = "ANAK NA LALAKI - HI-JO"
            "ANONG PANGALAN MO - COSA TU NOMBRE"-> holder.textView.text = "ANONG PANGALAN MO - CO-SA TU NOM-BRE"
            "APO NA LALAKI - NIETO"-> holder.textView.text = "APO NA LALAKI - NI-E-TO"
            "ASAWANG BABAE - MUJER"-> holder.textView.text = "ASAWANG BABAE - MU-JER"
            "ASAWANG LALAKI - MARIDO"-> holder.textView.text = "ASAWANG LALAKI - MA-RI-DO"
            "BABAE NA APO - NIETA"-> holder.textView.text = "BABAE NA APO - NI-E-TA"
            "BAYAW - CUÑADO"-> holder.textView.text = "BAYAW - CU-ÑA-DO"
            "BYENAN NA BABAE - SUEGRA"-> holder.textView.text =  "BYENAN NA BABAE - SU-E-GRA"
            "BYENAN NA LALAKE - SUEGRO"-> holder.textView.text = "BYENAN NA LALAKE - SU-E-GRO"
            "HIPAG - CUÑADA"-> holder.textView.text =  "HIPAG - CU-ÑA-DA"
            "INA - MADRE"-> holder.textView.text = "INA - MAD-RE"
            "KAPATID NA BABAE - HERMANA"-> holder.textView.text = "KAPATID NA BABAE - HER-MA-NA"
            "KAPATID NA LALAKI - HERMANO"-> holder.textView.text = "KAPATID NA LALAKI - HER-MA-NO"
            "LOLA - ABUELA"-> holder.textView.text = "LOLA - ABU-E-LA"
            "LOLO - ABUELO" -> holder.textView.text = "LOLO - ABU-E-LO"
            "MGA KAMAGANAK - PARIENTES"-> holder.textView.text = "MGA KAMAGANAK - PAR-I-EN-TES"
            "SAAN KA NAGMULA - DE DONDE TU"-> holder.textView.text = "SAAN KA NAGMULA - DE DON-DE TU"
            "TITA-TIA"-> holder.textView.text =  "TITA-TI-A"
            "TITO-TIO"-> holder.textView.text = "TITO-TI-O"
        }

        holder.count.text = "Tutorial $exactPos"
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

    fun setSuggestions(newSuggestions: List<Tutorial>) {
        tutorialList.clear()
        tutorialList.addAll(newSuggestions)
        notifyDataSetChanged()
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