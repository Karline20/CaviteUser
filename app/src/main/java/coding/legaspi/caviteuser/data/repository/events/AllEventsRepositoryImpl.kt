package coding.legaspi.caviteuser.data.repository.events

import android.util.Log
import coding.legaspi.caviteuser.data.model.aboutus.AboutUs
import coding.legaspi.caviteuser.data.model.aboutus.AboutUsOutput
import coding.legaspi.caviteuser.data.model.addevents.AddEvent
import coding.legaspi.caviteuser.data.model.auth.EmailVerified
import coding.legaspi.caviteuser.data.model.auth.EmailVerifiedOutput
import coding.legaspi.caviteuser.data.model.auth.LoginBody
import coding.legaspi.caviteuser.data.model.auth.LoginBodyOutput
import coding.legaspi.caviteuser.data.model.auth.SignBody
import coding.legaspi.caviteuser.data.model.auth.SignBodyOutput
import coding.legaspi.caviteuser.data.model.count.count
import coding.legaspi.caviteuser.data.model.events.AllModel
import coding.legaspi.caviteuser.data.model.eventsoutput.AllModelOutput
import coding.legaspi.caviteuser.data.model.favorites.Favorites
import coding.legaspi.caviteuser.data.model.favorites.FavoritesOutput
import coding.legaspi.caviteuser.data.model.profile.Profile
import coding.legaspi.caviteuser.data.model.profile.ProfileOutput
import coding.legaspi.caviteuser.data.model.ranking.PatchRank
import coding.legaspi.caviteuser.data.model.ranking.Ranking
import coding.legaspi.caviteuser.data.model.ranking.RankingOutput
import coding.legaspi.caviteuser.data.model.rating.AverageOutput
import coding.legaspi.caviteuser.data.model.rating.Existence
import coding.legaspi.caviteuser.data.model.rating.Rating
import coding.legaspi.caviteuser.data.model.rating.RatingOutput
import coding.legaspi.caviteuser.data.model.researchers.Researchers
import coding.legaspi.caviteuser.data.model.researchers.ResearchersOutput
import coding.legaspi.caviteuser.data.model.terms.Terms
import coding.legaspi.caviteuser.data.model.terms.TermsOutput
import coding.legaspi.caviteuser.data.model.tutorial.Tutorial
import coding.legaspi.caviteuser.data.model.tutorial.TutorialStatus
import coding.legaspi.caviteuser.data.model.tutorial.TutorialStatusOutput
import coding.legaspi.caviteuser.data.repository.events.datasource.AllEventsRemoteDataSource
import coding.legaspi.caviteuser.domain.repository.AllEventsRepository
import retrofit2.Response

class AllEventsRepositoryImpl(
    private val allEventsRemoteDataSource: AllEventsRemoteDataSource
): AllEventsRepository {

    override suspend fun searchEventsByCategory(searchQuery: String, eventCategory: String, category: String): List<AllModelOutput> {
        var eventcategoryList : List<AllModelOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.searchEventsByCategory(searchQuery, eventCategory, category)
            val body = response.body()
            if (body!=null){
                eventcategoryList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return eventcategoryList
    }

    override suspend fun getCategory(category: String): List<AllModelOutput> {
        var eventcategoryList: List<AllModelOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getCategory(category)
            val body = response.body()
            if (body!=null){
                eventcategoryList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return eventcategoryList
    }


    override suspend fun getAddEvents(): List<AddEvent> {
        var addEventList: List<AddEvent> = emptyList()
        Log.d("Home", "API")
        try {
            val response = allEventsRemoteDataSource.getAddEvent()
            val body = response.body()
            if (body!=null){
                addEventList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return addEventList
    }

    override suspend fun postEvents(allEvents: AllModel): Response<AllModelOutput> {
        return allEventsRemoteDataSource.postEvents(allEvents)
    }

    override suspend fun getAllEvents(): List<AllModelOutput> {
        var alleventsList: List<AllModelOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getAllEvents()
            val body = response.body()
            if (body!=null){
                alleventsList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return alleventsList
    }

    override suspend fun delEventsById(id: String): Response<Unit> {
        return allEventsRemoteDataSource.delEventsById(id)
    }

    override suspend fun patchEventsById(id: String, allModel: AllModel): Response<Unit> {
        return allEventsRemoteDataSource.patchEventsById(id, allModel)
    }


    override suspend fun getEventsById(id: String): Response<AllModelOutput> {
        return allEventsRemoteDataSource.getEventsById(id)
    }

    override suspend fun getEventsByCategory(eventcategory: String): List<AllModelOutput> {
        var eventcategoryList: List<AllModelOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getEventsByCategory(eventcategory)
            val body = response.body()
            if (body!=null){
                eventcategoryList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return eventcategoryList
    }

    override suspend fun countAllEvents(): Response<count> {
        return allEventsRemoteDataSource.countAllEvents()
    }

    override suspend fun countEventsByCategory(eventcategory: String): Response<count> {
        return allEventsRemoteDataSource.countEventsByCategory(eventcategory)
    }

    override suspend fun postRating(rating: Rating): Response<RatingOutput> {
        return allEventsRemoteDataSource.postRating(rating)
    }

    override suspend fun averageRating(eventid: String): Response<AverageOutput> {
        return allEventsRemoteDataSource.averageRating(eventid)
    }

    override suspend fun checkExistenceRating(existence: Existence): Response<Unit> {
        return allEventsRemoteDataSource.checkExistenceRating(existence)
    }

    override suspend fun getRating(): List<RatingOutput> {
        var ratingList: List<RatingOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getRating()
            val body = response.body()
            if (body!=null){
                ratingList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return ratingList
    }

    override suspend fun searchEvents(searchQuery: String, eventCategory: String): List<AllModelOutput> {
        var eventcategoryList : List<AllModelOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.searchEvents(searchQuery, eventCategory)
            val body = response.body()
            if (body!=null){
                eventcategoryList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return eventcategoryList
    }

    override suspend fun getRatingByEventId(eventid: String): List<RatingOutput> {
        var ratingList: List<RatingOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getRatingByEventId(eventid)
            val body = response.body()
            if (body!=null){
                ratingList = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return ratingList
    }

    override suspend fun getByUserId(userid: String): Response<ProfileOutput>{
        return allEventsRemoteDataSource.getByUserId(userid)
    }

    override suspend fun getTutorial(): List<Tutorial>{
        var tutorialList: List<Tutorial> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getTutorial()
            val body = response.body()
            if (body!=null){
                tutorialList = body
            }
        }catch (e: Exception){
            Log.e("MyTag", e.message.toString())
        }
        return tutorialList
    }

    override suspend fun postTutorialStatus(tutorialStatus: TutorialStatus): Response<TutorialStatusOutput> {
        return allEventsRemoteDataSource.postTutorialStatus(tutorialStatus)
    }

    override suspend fun getTutorialByUserId(
        tutorialid: String,
        userid: String
    ): Response<TutorialStatusOutput> {
        return allEventsRemoteDataSource.getTutorialByUserId(tutorialid, userid)
    }

    override suspend fun getTopLeaderBoards(): List<RankingOutput> {
        var getTopLeaderBoards: List<RankingOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getTopLeaderBoards()
            val body = response.body()
            if (body!=null){
                getTopLeaderBoards = body
            }
        }catch (e: Exception){
            Log.e("MyTag", e.message.toString())
        }
        return getTopLeaderBoards
    }

    override suspend fun postRank(ranking: Ranking): Response<RankingOutput> {
        return allEventsRemoteDataSource.postRank(ranking)
    }

    override suspend fun checkRanking(userid: String): Response<Unit> {
        return allEventsRemoteDataSource.checkRanking(userid)
    }

    override suspend fun getIdByuserid(userid: String): Response<RankingOutput> {
        return allEventsRemoteDataSource.getIdByuserid(userid)
    }

    override suspend fun patchRank(id: String, patchRank: PatchRank): Response<Unit> {
        return allEventsRemoteDataSource.patchRank(id, patchRank)
    }


    override suspend fun getUserId(userid: String): Response<ProfileOutput>{
        return allEventsRemoteDataSource.getByUserId(userid)
    }

    override suspend fun patchProfile(id: String, profile: Profile): Response<ProfileOutput>{
        return allEventsRemoteDataSource.patchProfile(id, profile)
    }

    override suspend fun getLogin(loginBody: LoginBody): Response<LoginBodyOutput> {
        return allEventsRemoteDataSource.loginUser(loginBody)
    }

    override suspend fun signup(signBody: SignBody): Response<SignBodyOutput>{
        return allEventsRemoteDataSource.signup(signBody)
    }

    override suspend fun updateEmailVerified(
        id: String,
        emailVerified: EmailVerified
    ): Response<EmailVerified> {
        return allEventsRemoteDataSource.updateEmailVerified(id, emailVerified)
    }

    override suspend fun isEmailVerified(id: String): Response<EmailVerifiedOutput> {
        return allEventsRemoteDataSource.isEmailVerified(id)
    }

    override suspend fun postProfile(profile: Profile): Response<ProfileOutput>{
        return allEventsRemoteDataSource.postProfile(profile)
    }

    override suspend fun postFavorites(favorites: Favorites): Response<FavoritesOutput> {
        return allEventsRemoteDataSource.postFavorites(favorites)
    }

    override suspend fun delFavorites(id: String): Response<Unit> {
        return allEventsRemoteDataSource.delFavorites(id)
    }

    override suspend fun getFavorites(userid: String): List<FavoritesOutput> {
        var getFavorites: List<FavoritesOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getFavorites(userid)
            val body = response.body()
            if (body!=null){
                getFavorites = body
            }
        }catch (e: Exception){
            Log.e("MyTag", e.message.toString())
        }
        return getFavorites
    }

    override suspend fun checkExistenceFavorites(existence: Existence): Response<Boolean> {
        return allEventsRemoteDataSource.checkExistenceFavorites(existence)
    }

    override suspend fun postAbout(aboutUs: AboutUs): Response<AboutUsOutput> {
        return allEventsRemoteDataSource.postAbout(aboutUs)
    }

    override suspend fun getAboutUs(id: String): Response<AboutUsOutput> {
        return allEventsRemoteDataSource.getAboutUs(id)
    }

    override suspend fun patchAboutUs(id: String, aboutUs: AboutUs): Response<AboutUsOutput> {
        return allEventsRemoteDataSource.patchAboutUs(id, aboutUs)
    }

    override suspend fun postTerms(terms: Terms): Response<TermsOutput> {
        return allEventsRemoteDataSource.postTerms(terms)
    }

    override suspend fun getTerms(id: String): Response<TermsOutput> {
        return allEventsRemoteDataSource.getTerms(id)
    }

    override suspend fun patchTerms(id: String, terms: Terms): Response<TermsOutput> {
        return allEventsRemoteDataSource.patchTerms(id, terms)
    }

    override suspend fun postResearch(researchers: Researchers): Response<ResearchersOutput> {
        return allEventsRemoteDataSource.postResearch(researchers)
    }

    override suspend fun getReserachers(): List<ResearchersOutput> {
        var getReserachers: List<ResearchersOutput> = emptyList()
        try {
            val response = allEventsRemoteDataSource.getReserachers()
            val body = response.body()
            if (body!=null){
                getReserachers = body
            }
        }catch (e:Exception){
            Log.i("MyTag", e.message.toString())
        }
        return getReserachers
    }

    override suspend fun patchResearchers(
        id: String,
        researchers: Researchers
    ): Response<ResearchersOutput> {
        return allEventsRemoteDataSource.patchResearchers(id, researchers)
    }

    override suspend fun deleteResearchers(id: String): Response<Unit> {
        return allEventsRemoteDataSource.deleteResearchers(id)
    }

}