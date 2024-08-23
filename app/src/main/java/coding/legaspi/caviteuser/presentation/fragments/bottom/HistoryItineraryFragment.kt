package coding.legaspi.caviteuser.presentation.fragments.bottom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.itenerary.Itinerary
import coding.legaspi.caviteuser.databinding.FragmentHistoryItineraryBinding
import coding.legaspi.caviteuser.utils.DateUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HistoryItineraryFragment(
    val itinerary: Itinerary
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentHistoryItineraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryItineraryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tripName.text = itinerary.itineraryName
        binding.dateSched.text = DateUtils.formatTimestampToDate(itinerary.scheduleDateTimestamp.toLong())
        binding.timeSched.text = DateUtils.formatTimestampToTime(itinerary.scheduleDateTimestamp.toLong())
        binding.destinationAddress.text = itinerary.itineraryPlace
        binding.currentAddress.text = itinerary.itineraryFrom
        binding.id.text = itinerary.id
        val isCompleted = if (itinerary.isTripCompleted) "COMPLETED" else ""
        binding.isTripCompleted.text = isCompleted
        binding.dateCompleted.text = DateUtils.formatTimestamp(itinerary.dateCompleted.toLong())

    }

}