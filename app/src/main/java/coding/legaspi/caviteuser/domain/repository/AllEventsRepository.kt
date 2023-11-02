package coding.legaspi.caviteuser.domain.repository

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
import retrofit2.Response
import retrofit2.http.Query

interface AllEventsRepository {

    suspend fun getCategory(eventcategory: String): List<AllModelOutput>

    suspend fun searchEventsByCategory(searchQuery: String, eventCategory: String, category: String): List<AllModelOutput>

    suspend fun postEvents(cafe: AllModel): Response<AllModelOutput>

    suspend fun getAddEvents():List<AddEvent>?

    suspend fun getAllEvents(): List<AllModelOutput>

    suspend fun delEventsById(id: String): Response<Unit>

    suspend fun patchEventsById(id: String, allModel: AllModel): Response<Unit>

    suspend fun getEventsById(id: String): Response<AllModelOutput>

    suspend fun getEventsByCategory(eventcategory: String): List<AllModelOutput>

    suspend fun countAllEvents(): Response<count>

    suspend fun countEventsByCategory(eventcategory: String): Response<count>

    suspend fun postRating(rating: Rating): Response<RatingOutput>

    suspend fun averageRating(eventid: String): Response<AverageOutput>

    suspend fun checkExistenceRating(existence: Existence): Response<Unit>

    suspend fun getRating(): List<RatingOutput>

    suspend fun searchEvents(searchQuery: String, eventCategory: String): List<AllModelOutput>

    suspend fun getRatingByEventId(eventid: String): List<RatingOutput>

    suspend fun getByUserId(userid: String): Response<ProfileOutput>

    suspend fun getTutorial(): List<Tutorial>

    suspend fun postTutorialStatus(tutorialStatus: TutorialStatus): Response<TutorialStatusOutput>

    suspend fun getTutorialByUserId(tutorialid: String, userid: String): Response<TutorialStatusOutput>

    suspend fun getTopLeaderBoards():List<RankingOutput>

    suspend fun postRank(ranking: Ranking): Response<RankingOutput>

    suspend fun checkRanking(userid: String): Response<Unit>

    suspend fun getIdByuserid(userid: String): Response<RankingOutput>

    suspend fun patchRank(id: String, patchRank: PatchRank): Response<Unit>

    suspend fun getUserId(userid: String): Response<ProfileOutput>

    suspend fun patchProfile(id: String, profile: Profile): Response<ProfileOutput>

    suspend fun getLogin(loginBody: LoginBody):Response<LoginBodyOutput>

    suspend fun signup(signBody: SignBody): Response<SignBodyOutput>

    suspend fun updateEmailVerified(id: String, emailVerified: EmailVerified): Response<EmailVerified>

    suspend fun isEmailVerified(id: String): Response<EmailVerifiedOutput>

    suspend fun postProfile(profile: Profile): Response<ProfileOutput>

    suspend fun postFavorites(favorites: Favorites): Response<FavoritesOutput>

    suspend fun delFavorites(id: String): Response<Unit>

    suspend fun getFavorites(userid: String): List<FavoritesOutput>

    suspend fun checkExistenceFavorites(existence: Existence): Response<Boolean>

    //aboutus
    suspend fun postAbout(aboutUs: AboutUs): Response<AboutUsOutput>

    suspend fun getAboutUs(id: String): Response<AboutUsOutput>

    suspend fun patchAboutUs(id: String, aboutUs: AboutUs): Response<AboutUsOutput>

    //terms
    suspend fun postTerms(terms: Terms): Response<TermsOutput>

    suspend fun getTerms(id: String,): Response<TermsOutput>

    suspend fun patchTerms(id: String, terms: Terms): Response<TermsOutput>

    //researcher
    suspend fun postResearch(researchers: Researchers): Response<ResearchersOutput>

    suspend fun getReserachers(): List<ResearchersOutput>

    suspend fun patchResearchers(id: String, researchers: Researchers): Response<ResearchersOutput>

    suspend fun deleteResearchers(id: String): Response<Unit>


}