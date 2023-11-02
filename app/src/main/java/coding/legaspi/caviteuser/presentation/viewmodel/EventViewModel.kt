package coding.legaspi.caviteuser.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import coding.legaspi.caviteuser.data.model.aboutus.AboutUs
import coding.legaspi.caviteuser.data.model.auth.EmailVerified
import coding.legaspi.caviteuser.data.model.auth.LoginBody
import coding.legaspi.caviteuser.data.model.auth.SignBody
import coding.legaspi.caviteuser.data.model.events.AllModel
import coding.legaspi.caviteuser.data.model.favorites.Favorites
import coding.legaspi.caviteuser.data.model.profile.Profile
import coding.legaspi.caviteuser.data.model.ranking.PatchRank
import coding.legaspi.caviteuser.data.model.ranking.Ranking
import coding.legaspi.caviteuser.data.model.rating.Existence
import coding.legaspi.caviteuser.data.model.rating.Rating
import coding.legaspi.caviteuser.data.model.researchers.Researchers
import coding.legaspi.caviteuser.data.model.terms.Terms
import coding.legaspi.caviteuser.data.model.tutorial.TutorialStatus
import coding.legaspi.caviteuser.domain.getusecase.GetEventsUseCase

class EventViewModel(
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel(){

    fun getCategory(category: String) = liveData {
        val getCategory = getEventsUseCase.getCategory(category)
        emit(getCategory)
    }

    fun searchCategory(searchQuery: String, eventCategory: String, category: String) = liveData {
        val searchEvents = getEventsUseCase.searchEventsByCategory(searchQuery, eventCategory, category)
        emit(searchEvents)
    }

    fun getAddEvents() = liveData {
        val addEventList = getEventsUseCase.execute()
        emit(addEventList)
    }

    fun postEvents(events: AllModel) = liveData {
        val events = getEventsUseCase.postEvents(events)
        emit(events)
    }

    fun getAllEvents() = liveData {
        val getAllEvents = getEventsUseCase.getAllEvents()
        emit(getAllEvents)
    }

    fun delEventsById(id: String) = liveData {
        val delEventsById = getEventsUseCase.delEventsById(id)
        emit(delEventsById)
    }

    //patch
    fun patchEventsById(id: String, allModel: AllModel) = liveData {
        val patchEventsById = getEventsUseCase.patchEventsById(id, allModel)
        emit(patchEventsById)
    }

    fun countAllEvents() = liveData {
        val countAllEvents = getEventsUseCase.countAllEvents()
        emit(countAllEvents)
    }

    fun getEventsById(id: String) = liveData {
        val getEventsById = getEventsUseCase.getEventsById(id)
        emit(getEventsById)
    }

    fun getEventsByCategory(eventcategory: String) = liveData {
        val getEventsByCategory = getEventsUseCase.getEventsByCategory(eventcategory)
        emit(getEventsByCategory)
    }

    fun countEventsByCategory(eventcategory: String) = liveData {
        val countEventsByCategory = getEventsUseCase.countEventsByCategory(eventcategory)
        emit(countEventsByCategory)
    }

    fun postRating(rating: Rating) = liveData {
        val postRating = getEventsUseCase.postRating(rating)
        emit(postRating)
    }

    fun averageRating(eventid: String) = liveData {
        val averageRating = getEventsUseCase.averageRating(eventid)
        emit(averageRating)
    }

    fun checkExistenceRating(existence: Existence) = liveData {
        val checkExistenceRating = getEventsUseCase.checkExistenceRating(existence)
        emit(checkExistenceRating)
    }

    fun getRating() = liveData {
        val getRating = getEventsUseCase.getRating()
        emit(getRating)
    }

    fun searchEvents(searchQuery: String, eventCategory: String) = liveData {
        val searchEvents = getEventsUseCase.searchEvents(searchQuery, eventCategory)
        emit(searchEvents)
    }

    fun getRatingByEventId(eventid: String) = liveData {
        val getRating = getEventsUseCase.getRatingByEventId(eventid)
        emit(getRating)
    }

    fun getByUserId(userid: String) = liveData {
        val getByUserid = getEventsUseCase.getByUserId(userid)
        emit(getByUserid)
    }

    fun getTutorial() = liveData {
        val getTutorial = getEventsUseCase.getTutorial()
        emit(getTutorial)
    }

    fun postTutorialStatus(tutorialStatus: TutorialStatus) = liveData {
        val postTutorialStatus = getEventsUseCase.postTutorialStatus(tutorialStatus)
        emit(postTutorialStatus)
    }

    fun getTutorialByUserId(tutorialid: String, userid: String) = liveData {
        val getTutorialByUserId = getEventsUseCase.getTutorialByUserId(tutorialid, userid)
        emit(getTutorialByUserId)
    }

    fun getTopLeaderBoards() = liveData {
        val getTopLeaderBoards = getEventsUseCase.getTopLeaderBoards()
        emit(getTopLeaderBoards)
    }

    fun postRank(ranking: Ranking) = liveData {
        val postRank = getEventsUseCase.postRank(ranking)
        emit(postRank)
    }

    fun checkRanking(userid: String) = liveData {
        val checkRanking = getEventsUseCase.checkRanking(userid)
        emit(checkRanking)
    }

    fun getIdByuserid(userid: String) = liveData {
        val getIdByuserid = getEventsUseCase.getIdByuserid(userid)
        emit(getIdByuserid)
    }

    fun patchRank(id: String, patchRank: PatchRank) = liveData {
        val patchRank = getEventsUseCase.patchRank(id, patchRank)
        emit(patchRank)
    }

    fun getUserId(userid: String) = liveData {
        val userid = getEventsUseCase.getByUserId(userid)
        emit(userid)
    }

    fun patchProfile(userid: String, profile: Profile) = liveData {
        val profile = getEventsUseCase.patchProfile(userid, profile)
        emit(profile)
    }

    fun getLoginEventUseCase(loginBody: LoginBody) = liveData {
        val login = getEventsUseCase.getLogin(loginBody)
        emit(login)
    }

    fun getLoginEventUseCase(signBody: SignBody) = liveData {
        val signup = getEventsUseCase.signup(signBody)
        emit(signup)
    }

    fun updateEmailVerified(id: String, emailVerified: EmailVerified) = liveData {
        val update = getEventsUseCase.updateEmailVerified(id, emailVerified)
        emit(update)
    }

    fun isEmailVerified(id: String) = liveData {
        val isVerified = getEventsUseCase.isEmailVerified(id)
        emit(isVerified)
    }

    fun postProfile(profile: Profile) = liveData {
        val profile = getEventsUseCase.postProfile(profile)
        emit(profile)
    }

    fun postFavorites(favorites: Favorites) = liveData {
        val postFavorites = getEventsUseCase.postFavorites(favorites)
        emit(postFavorites)
    }

    fun delFavorites(id: String) = liveData {
        val delFavorites = getEventsUseCase.delFavorites(id)
        emit(delFavorites)
    }

    fun getFavorites(userid: String) = liveData {
        val getFavorites = getEventsUseCase.getFavorites(userid)
        emit(getFavorites)
    }

    fun checkExistenceFavorites(existence: Existence) = liveData {
        val checkExistenceFavorites = getEventsUseCase.checkExistenceFavorites(existence)
        emit(checkExistenceFavorites)
    }

    fun postAbout(aboutUs: AboutUs) = liveData {
        val postAbout = getEventsUseCase.postAbout(aboutUs)
        emit(postAbout)
    }

    fun getAboutUs(id: String) = liveData {
        val getAboutUs = getEventsUseCase.getAboutUs(id)
        emit(getAboutUs)
    }

    fun patchAboutUs(id: String, aboutUs: AboutUs) = liveData {
        val patchAboutUs = getEventsUseCase.patchAboutUs(id, aboutUs)
        emit(patchAboutUs)
    }

    fun postTerms(terms: Terms) = liveData {
        val postTerms = getEventsUseCase.postTerms(terms)
        emit(postTerms)
    }

    fun getTerms(id: String) = liveData {
        val getTerms = getEventsUseCase.getTerms(id)
        emit(getTerms)
    }

    fun patchTerms(id: String, terms: Terms) = liveData {
        val patchTerms = getEventsUseCase.patchTerms(id, terms)
        emit(patchTerms)
    }

    fun postResearch(researchers: Researchers) = liveData {
        val postResearch = getEventsUseCase.postResearch(researchers)
        emit(postResearch)
    }

    fun getReserachers() = liveData {
        val getReserachers = getEventsUseCase.getReserachers()
        emit(getReserachers)
    }

    fun patchResearchers(
        id: String,
        researchers: Researchers
    ) = liveData {
        val patchResearchers = getEventsUseCase.patchResearchers(id, researchers)
        emit(patchResearchers)
    }

    fun deleteResearchers(id: String) = liveData {
        val deleteResearchers = getEventsUseCase.deleteResearchers(id)
        emit(deleteResearchers)
    }
}