package coding.legaspi.caviteuser.domain.getusecase

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
import coding.legaspi.caviteuser.domain.repository.AllEventsRepository
import retrofit2.Response

class GetEventsUseCase(private val allEventsRepository: AllEventsRepository) {

    suspend fun searchEventsByCategory(
        searchQuery: String,
        eventCategory: String,
        category: String
    ): List<AllModelOutput> = allEventsRepository.searchEventsByCategory(searchQuery, eventCategory, category)

    suspend fun getCategory(category: String): List<AllModelOutput> = allEventsRepository.getCategory(category)

    suspend fun execute():List<AddEvent>? = allEventsRepository.getAddEvents()

    suspend fun postEvents(events: AllModel):Response<AllModelOutput> = allEventsRepository.postEvents(events)

    suspend fun getAllEvents(): List<AllModelOutput> = allEventsRepository.getAllEvents()

    suspend fun delEventsById(id: String): Response<Unit> = allEventsRepository.delEventsById(id)

    suspend fun patchEventsById(id: String, allModel: AllModel): Response<Unit> = allEventsRepository.patchEventsById(id, allModel)

    suspend fun countAllEvents():Response<count> = allEventsRepository.countAllEvents()

    suspend fun getEventsById(id: String): Response<AllModelOutput> = allEventsRepository.getEventsById(id)

    suspend fun getEventsByCategory(eventcategory: String):  List<AllModelOutput> = allEventsRepository.getEventsByCategory(eventcategory)

    suspend fun countEventsByCategory(eventcategory: String): Response<count> = allEventsRepository.countEventsByCategory(eventcategory)

    suspend fun postRating(rating: Rating): Response<RatingOutput> = allEventsRepository.postRating(rating)

    suspend fun averageRating(eventid: String): Response<AverageOutput> = allEventsRepository.averageRating(eventid)

    suspend fun checkExistenceRating(existence: Existence): Response<Unit> = allEventsRepository.checkExistenceRating(existence)

    suspend fun getRating(): List<RatingOutput> = allEventsRepository.getRating()

    suspend fun searchEvents(searchQuery: String, eventCategory: String): List<AllModelOutput> = allEventsRepository.searchEvents(searchQuery, eventCategory)

    suspend fun getRatingByEventId(eventid: String): List<RatingOutput> = allEventsRepository.getRatingByEventId(eventid)

    suspend fun getByUserId(userid: String): Response<ProfileOutput> = allEventsRepository.getByUserId(userid)

    suspend fun getTutorial(): List<Tutorial> = allEventsRepository.getTutorial()

    suspend fun postTutorialStatus(tutorialStatus: TutorialStatus): Response<TutorialStatusOutput> = allEventsRepository.postTutorialStatus(tutorialStatus)

    suspend fun getTutorialByUserId(tutorialid: String, userid: String): Response<TutorialStatusOutput> = allEventsRepository.getTutorialByUserId(tutorialid, userid)

    suspend fun getTopLeaderBoards():List<RankingOutput> = allEventsRepository.getTopLeaderBoards()

    suspend fun postRank(ranking: Ranking): Response<RankingOutput> = allEventsRepository.postRank(ranking)

    suspend fun checkRanking(userid: String): Response<Unit> = allEventsRepository.checkRanking(userid)

    suspend fun getIdByuserid(userid: String): Response<RankingOutput> = allEventsRepository.getIdByuserid(userid)

    suspend fun patchRank(id: String, patchRank: PatchRank): Response<Unit> = allEventsRepository.patchRank(id, patchRank)

    suspend fun getUserId(userid: String): Response<ProfileOutput> = allEventsRepository.getByUserId(userid)

    suspend fun patchProfile(id: String, profile: Profile): Response<ProfileOutput> = allEventsRepository.patchProfile(id, profile)

    suspend fun getLogin(loginBody: LoginBody):Response<LoginBodyOutput> = allEventsRepository.getLogin(loginBody)

    suspend fun signup(signBody: SignBody): Response<SignBodyOutput> =  allEventsRepository.signup(signBody)

    suspend fun updateEmailVerified(id: String, emailVerified: EmailVerified): Response<EmailVerified> = allEventsRepository.updateEmailVerified(id, emailVerified)

    suspend fun isEmailVerified(id: String): Response<EmailVerifiedOutput> = allEventsRepository.isEmailVerified(id)

    suspend fun postProfile(profile: Profile): Response<ProfileOutput> = allEventsRepository.postProfile(profile)

    suspend fun postFavorites(favorites: Favorites): Response<FavoritesOutput> = allEventsRepository.postFavorites(favorites)

    suspend fun delFavorites(id: String): Response<Unit> = allEventsRepository.delFavorites(id)

    suspend fun getFavorites(userid: String): List<FavoritesOutput> = allEventsRepository.getFavorites(userid)

    suspend fun checkExistenceFavorites(existence: Existence): Response<Boolean> = allEventsRepository.checkExistenceFavorites(existence)

    suspend fun postAbout(aboutUs: AboutUs): Response<AboutUsOutput> = allEventsRepository.postAbout(aboutUs)

    suspend fun getAboutUs(id: String): Response<AboutUsOutput> = allEventsRepository.getAboutUs(id)

    suspend fun patchAboutUs(id: String, aboutUs: AboutUs): Response<AboutUsOutput> = allEventsRepository.patchAboutUs(id, aboutUs)

    suspend fun postTerms(terms: Terms): Response<TermsOutput> = allEventsRepository.postTerms(terms)

    suspend fun getTerms(id: String): Response<TermsOutput> = allEventsRepository.getTerms(id)

    suspend fun patchTerms(id: String, terms: Terms): Response<TermsOutput> = allEventsRepository.patchTerms(id, terms)

    suspend fun postResearch(researchers: Researchers): Response<ResearchersOutput> = allEventsRepository.postResearch(researchers)

    suspend fun getReserachers(): List<ResearchersOutput> = allEventsRepository.getReserachers()

    suspend fun patchResearchers(
        id: String,
        researchers: Researchers
    ): Response<ResearchersOutput> = allEventsRepository.patchResearchers(id, researchers)

    suspend fun deleteResearchers(id: String): Response<Unit> = allEventsRepository.deleteResearchers(id)

}
