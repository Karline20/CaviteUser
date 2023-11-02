package coding.legaspi.caviteuser.data.repository.events.datasourceImpl

import coding.legaspi.caviteuser.data.api.TMDBService
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
import retrofit2.Response
import retrofit2.http.Path

class AllEventsRemoteDataSourceImpl(
    private val tmdbService: TMDBService
): AllEventsRemoteDataSource {

    override suspend fun getCategory(category: String): Response<List<AllModelOutput>> {
        return tmdbService.getCategory(category)
    }

    override suspend fun searchEventsByCategory(
        searchQuery: String,
        eventCategory: String,
        category: String
    ): Response<List<AllModelOutput>> {
        return tmdbService.searchEventsByCategory(searchQuery, eventCategory, category)
    }


    override suspend fun getAddEvent(): Response<List<AddEvent>> = tmdbService.addEvent()

    override suspend fun postEvents(events: AllModel): Response<AllModelOutput> {
        return tmdbService.postEvents(events)
    }

    override suspend fun getAllEvents(): Response<List<AllModelOutput>> {
        return tmdbService.getAllEvents()
    }

    override suspend fun delEventsById(id: String): Response<Unit> {
        return tmdbService.delEventsById(id)
    }

    override suspend fun patchEventsById(id: String, allModel: AllModel): Response<Unit> {
        return tmdbService.patchEventsById(id, allModel)
    }

    override suspend fun countAllEvents(): Response<count> {
        return tmdbService.countAllEvents()
    }

    override suspend fun getEventsById(id: String): Response<AllModelOutput> {
        return tmdbService.getEventsById(id)
    }

    override suspend fun getEventsByCategory(eventcategory: String): Response<List<AllModelOutput>> {
        return tmdbService.getEventsByCategory(eventcategory)
    }

    override suspend fun countEventsByCategory(eventcategory: String): Response<count> {
        return tmdbService.countEventsByCategory(eventcategory)
    }

    override suspend fun postRating(rating: Rating): Response<RatingOutput> {
        return tmdbService.postRating(rating)
    }

    override suspend fun averageRating(eventid: String): Response<AverageOutput> {
        return tmdbService.averageRating(eventid)
    }

    override suspend fun checkExistenceRating(existence: Existence): Response<Unit> {
        return tmdbService.checkExistenceRating(existence)
    }

    override suspend fun getRating(): Response<List<RatingOutput>> {
        return tmdbService.getRating()
    }

    override suspend fun searchEvents(searchQuery: String, eventCategory: String): Response<List<AllModelOutput>> {
        return tmdbService.searchEvents(searchQuery, eventCategory)
    }

    override suspend fun getRatingByEventId(eventid: String): Response<List<RatingOutput>> {
        return tmdbService.getRatingByEventId(eventid)
    }

    override suspend fun getByUserId(userid: String): Response<ProfileOutput> = tmdbService.getByUserId(userid)

    override suspend fun getTutorial(): Response<List<Tutorial>> {
        return tmdbService.getTutorial()
    }

    override suspend fun postTutorialStatus(tutorialStatus: TutorialStatus): Response<TutorialStatusOutput> {
        return tmdbService.postTutorialStatus(tutorialStatus)
    }

    override suspend fun getTutorialByUserId(
        tutorialid: String,
        userid: String
    ): Response<TutorialStatusOutput> {
        return tmdbService.getTutorialByUserId(tutorialid, userid)
    }

    override suspend fun getTopLeaderBoards(): Response<List<RankingOutput>> {
        return tmdbService.getTopLeaderBoards()
    }

    override suspend fun postRank(ranking: Ranking): Response<RankingOutput> {
        return tmdbService.postRank(ranking)
    }

    override suspend fun checkRanking(userid: String): Response<Unit> {
        return tmdbService.checkRanking(userid)
    }

    override suspend fun getIdByuserid(userid: String): Response<RankingOutput> {
        return tmdbService.getIdByuserid(userid)
    }

    override suspend fun patchRank(id: String, patchRank: PatchRank): Response<Unit> {
        return tmdbService.patchRank(id, patchRank)
    }

    override suspend fun getUserId(userid: String): Response<ProfileOutput> = tmdbService.getByUserId(userid)

    override suspend fun patchProfile(id: String, profile: Profile): Response<ProfileOutput> = tmdbService.patchProfile(id, profile)

    override suspend fun loginUser(loginBody: LoginBody): Response<LoginBodyOutput> = tmdbService.loginUser(loginBody)
    override suspend fun signup(signBody: SignBody): Response<SignBodyOutput> = tmdbService.signup(signBody)
    override suspend fun updateEmailVerified(id: String, emailVerified: EmailVerified): Response<EmailVerified> = tmdbService.updateEmailVerified(id, emailVerified)
    override suspend fun isEmailVerified(id: String): Response<EmailVerifiedOutput> = tmdbService.isEmailVerified(id)
    override suspend fun postProfile(profile: Profile): Response<ProfileOutput> = tmdbService.postProfile(profile)

    override suspend fun postFavorites(favorites: Favorites): Response<FavoritesOutput> = tmdbService.postFavorites(favorites)

    override suspend fun delFavorites(id: String): Response<Unit> = tmdbService.delFavorites(id)

    override suspend fun getFavorites(userid: String): Response<List<FavoritesOutput>> = tmdbService.getFavorites(userid)

    override suspend fun checkExistenceFavorites(existence: Existence): Response<Boolean> = tmdbService.checkExistenceFavorites(existence)

    override suspend fun postAbout(aboutUs: AboutUs): Response<AboutUsOutput> {
        return tmdbService.postAbout(aboutUs)
    }

    override suspend fun getAboutUs(id: String): Response<AboutUsOutput> {
        return tmdbService.getAboutUs(id)
    }

    override suspend fun patchAboutUs(id: String, aboutUs: AboutUs): Response<AboutUsOutput> {
        return tmdbService.patchAboutUs(id, aboutUs)
    }

    override suspend fun postTerms(terms: Terms): Response<TermsOutput> {
        return tmdbService.postTerms(terms)
    }

    override suspend fun getTerms(id: String): Response<TermsOutput> {
        return tmdbService.getTerms(id)
    }

    override suspend fun patchTerms(id: String, terms: Terms): Response<TermsOutput> {
        return tmdbService.patchTerms(id, terms)
    }

    override suspend fun postResearch(researchers: Researchers): Response<ResearchersOutput> {
        return tmdbService.postResearch(researchers)
    }

    override suspend fun getReserachers(): Response<List<ResearchersOutput>> {
        return tmdbService.getReserachers()
    }

    override suspend fun patchResearchers(
        id: String,
        researchers: Researchers
    ): Response<ResearchersOutput> {
        return tmdbService.patchResearchers(id, researchers)
    }

    override suspend fun deleteResearchers(id: String): Response<Unit> {
        return tmdbService.deleteResearchers(id)
    }


}