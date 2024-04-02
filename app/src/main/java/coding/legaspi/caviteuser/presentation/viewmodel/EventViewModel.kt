package coding.legaspi.caviteuser.presentation.viewmodel

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import coding.legaspi.caviteuser.Result
import java.io.IOException

class EventViewModel(
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel(){

    fun getCategory(category: String) = liveData {
        try {
            val getCategory = getEventsUseCase.getCategory(category)
            emit(Result.Success(getCategory))
        }catch (ioException: IOException) {
            emit(Result.Error(ioException))
            Log.e("Check Result", "ioException $ioException")
        } catch (exception: Exception) {
            emit(Result.Error(exception))
            Log.e("Check Result", "exception $exception")
        }
    }

    fun searchCategory(searchQuery: String, eventCategory: String, category: String) = liveData {
        try {
            val searchEvents = getEventsUseCase.searchEventsByCategory(searchQuery, eventCategory, category)
            emit(Result.Success(searchEvents))
        }catch (ioException: IOException) {
            emit(Result.Error(ioException))
            Log.e("Check Result", "ioException $ioException")
        } catch (exception: Exception) {
            emit(Result.Error(exception))
            Log.e("Check Result", "exception $exception")
        }

    }

    fun getAddEvents() = liveData {
        try {
            val addEventList = getEventsUseCase.execute()
            emit(Result.Success(addEventList))
        }catch (ioException: IOException) {
            emit(Result.Error(ioException))
            Log.e("Check Result", "ioException $ioException")
        } catch (exception: Exception) {
            emit(Result.Error(exception))
            Log.e("Check Result", "exception $exception")
        }
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

    fun getEventsById(id: String) = liveData(Dispatchers.IO) {
        try {
            val getEventsById = getEventsUseCase.getEventsById(id)
            if (getEventsById.isSuccessful) {
                getEventsById.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${getEventsById.code()} ${getEventsById.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun getEventsByCategory(eventcategory: String) = liveData {
        try {
            val getEventsByCategory = getEventsUseCase.getEventsByCategory(eventcategory)
            emit(Result.Success(getEventsByCategory))
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }

    }

    fun countEventsByCategory(eventcategory: String) = liveData(Dispatchers.IO) {
        try {
            val countEventsByCategory = getEventsUseCase.countEventsByCategory(eventcategory)
            if (countEventsByCategory.isSuccessful) {
                // Emit the response body if the request was successful
                countEventsByCategory.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${countEventsByCategory.code()} ${countEventsByCategory.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            // Handle other exceptions
            emit(Result.Error(exception))
        }
    }

    fun postRating(rating: Rating) = liveData(Dispatchers.IO) {
        try {
            val postRating = getEventsUseCase.postRating(rating)
            emit(postRating)
            if (postRating.isSuccessful) {
                // Emit the response body if the request was successful
                postRating.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                // Emit an error if the request was not successful
                emit(Result.Error(IOException("Error: ${postRating.code()} ${postRating.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            // Handle other exceptions
            emit(Result.Error(exception))
        }
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
        try {
            val searchEvents = getEventsUseCase.searchEvents(searchQuery, eventCategory)
            emit(Result.Success(searchEvents))
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun getRatingByEventId(eventid: String) = liveData {
        try {
            val getRating = getEventsUseCase.getRatingByEventId(eventid)
            emit(Result.Success(getRating))
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }

    }

    fun getByUserId(userid: String) = liveData(Dispatchers.IO) {
        try {
            val getByUserid = getEventsUseCase.getByUserId(userid)
            if (getByUserid.isSuccessful) {
                // Emit the response body if the request was successful
                getByUserid.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                // Emit an error if the request was not successful
                emit(Result.Error(IOException("Error: ${getByUserid.code()} ${getByUserid.message()}")))
                Log.e("Check Result", "Error: ${getByUserid.code()} ${getByUserid.message()}")
            }
        }catch (ioException: IOException) {
            emit(Result.Error(ioException))
            Log.e("Check Result", "ioException $ioException")
        } catch (exception: Exception) {
            // Handle other exceptions
            emit(Result.Error(exception))
            Log.e("Check Result", "exception $exception")
        }
    }

    fun getTutorial() = liveData {
        try {
            val getTutorial = getEventsUseCase.getTutorial()
            emit(Result.Success(getTutorial))
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun postTutorialStatus(tutorialStatus: TutorialStatus) = liveData(Dispatchers.IO) {
        try {
            val postTutorialStatus = getEventsUseCase.postTutorialStatus(tutorialStatus)
            if (postTutorialStatus.isSuccessful) {
                postTutorialStatus.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${postTutorialStatus.code()} ${postTutorialStatus.message()}")))
            }
        }catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun getTutorialByUserId(tutorialid: String, userid: String) = liveData(Dispatchers.IO) {
        try {
            val getTutorialByUserId = getEventsUseCase.getTutorialByUserId(tutorialid, userid)
            if (getTutorialByUserId.isSuccessful) {
                getTutorialByUserId.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${getTutorialByUserId.code()} ${getTutorialByUserId.message()}")))
            }
        }catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }

    }

    fun getTopLeaderBoards() = liveData {
        try {
            val getTopLeaderBoards = getEventsUseCase.getTopLeaderBoards()
            emit(Result.Success(getTopLeaderBoards))
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }

    }

    fun postRank(ranking: Ranking) = liveData(Dispatchers.IO) {
        try {
            val postRank = getEventsUseCase.postRank(ranking)
            if (postRank.isSuccessful) {
                postRank.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${postRank.code()} ${postRank.message()}")))
            }
        }catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }

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

    fun getUserId(userid: String) = liveData(Dispatchers.IO) {
        try {
            val user = getEventsUseCase.getByUserId(userid)
            if (user.isSuccessful) {
                // Emit the response body if the request was successful
                user.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                // Emit an error if the request was not successful
                emit(Result.Error(IOException("Error: ${user.code()} ${user.message()}")))
                Log.e("Check Result", "Error: ${user.code()} ${user.message()}")
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
            Log.e("Check Result", "ioException $ioException")
        } catch (exception: Exception) {
            // Handle other exceptions
            emit(Result.Error(exception))
            Log.e("Check Result", "exception $exception")
        }
    }

    fun patchProfile(userid: String, profile: Profile) = liveData {
        val profile = getEventsUseCase.patchProfile(userid, profile)
        emit(profile)
    }

    fun getLoginEventUseCase(loginBody: LoginBody) = liveData(Dispatchers.IO) {
        try {
            val login = getEventsUseCase.getLogin(loginBody)
            //emit(login)
            if (login.isSuccessful) {
                // Emit the response body if the request was successful
                login.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                // Emit an error if the request was not successful
                emit(Result.Error(IOException("Error: ${login.code()} ${login.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            // Handle other exceptions
            emit(Result.Error(exception))
        }

    }

    fun getLoginEventUseCase(signBody: SignBody) = liveData(Dispatchers.IO) {
        try {
            val signup = getEventsUseCase.signup(signBody)
            if (signup.isSuccessful) {
                signup.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${signup.code()} ${signup.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            // Handle other exceptions
            emit(Result.Error(exception))
        }
    }

    fun updateEmailVerified(id: String, emailVerified: EmailVerified) = liveData {
        val update = getEventsUseCase.updateEmailVerified(id, emailVerified)
        emit(update)
    }

    fun isEmailVerified(id: String) = liveData {
        val isVerified = getEventsUseCase.isEmailVerified(id)
        emit(isVerified)
    }

    fun postProfile(profile: Profile) = liveData(Dispatchers.IO) {
        try {
            val profile = getEventsUseCase.postProfile(profile)
            if (profile.isSuccessful) {
                profile.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${profile.code()} ${profile.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun postFavorites(favorites: Favorites) = liveData(Dispatchers.IO) {
        try {
            val postFavorites = getEventsUseCase.postFavorites(favorites)
            if (postFavorites.isSuccessful) {
                postFavorites.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${postFavorites.code()} ${postFavorites.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }

    }

    fun delFavorites(id: String) = liveData(Dispatchers.IO) {
        try {
            val delFavorites = getEventsUseCase.delFavorites(id)
            if (delFavorites.isSuccessful) {
                delFavorites.body()?.let {
                    emit(Result.Success(it))
                }
            } else {
                emit(Result.Error(IOException("Error: ${delFavorites.code()} ${delFavorites.message()}")))
            }
        } catch (ioException: IOException) {
            emit(Result.Error(ioException))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
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