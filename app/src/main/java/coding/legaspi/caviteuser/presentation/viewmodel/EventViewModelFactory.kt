package coding.legaspi.caviteuser.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.domain.getusecase.GetEventsUseCase

class EventViewModelFactory(
    private val getEventsUseCase: GetEventsUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventViewModel(getEventsUseCase) as T
    }
}