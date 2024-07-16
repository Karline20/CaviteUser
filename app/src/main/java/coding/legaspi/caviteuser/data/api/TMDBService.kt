package coding.legaspi.caviteuser.data.api

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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {

    @GET("/allevents/search/category")
    suspend fun searchEventsByCategory(@Query("q") searchQuery: String, @Query("eventcategory") eventcategory: String, @Query("category") category: String): Response<List<AllModelOutput>>

    @GET("/alleventsByCategoryToo/{category}")
    suspend fun getCategory(@Path("category") category: String): Response<List<AllModelOutput>>

    //auth
    @POST("users/login")
    suspend fun loginUser(@Body loginBody: LoginBody): Response<LoginBodyOutput>
    //auth
    @POST("signup")
    suspend fun signup(@Body loginBody: SignBody): Response<SignBodyOutput>

    @POST("/users/update-email-verified/{id}")
    suspend fun updateEmailVerified(@Path("id") id: String, @Body emailVerified: EmailVerified): Response<EmailVerified>

    @GET("/users/check-email-verified/{id}")
    suspend fun isEmailVerified(@Path("id") id: String): Response<EmailVerifiedOutput>

    //profile
    @POST("/profile")
    suspend fun postProfile(@Body profile: Profile): Response<ProfileOutput>

    @GET("/profile/by-userid/{userid}")
    suspend fun getByUserId(@Path("userid") userid: String): Response<ProfileOutput>

    @PATCH("/profile/{id}")
    suspend fun patchProfile(@Path("id") id: String, @Body profile: Profile): Response<ProfileOutput>

    //Post Events
    @POST("/allevents")
    suspend fun postEvents(@Body cafe: AllModel): Response<AllModelOutput>

    //events
    @GET("addevents")
    suspend fun addEvent(): Response<List<AddEvent>>

    //get Events
    @GET("/allevents")
    suspend fun getAllEvents(): Response<List<AllModelOutput>>

    @GET("/allevents/search")
    suspend fun searchEvents(@Query("q") searchQuery: String, @Query("eventcategory") eventcategory: String): Response<List<AllModelOutput>>

    //get count all
    @GET("/allevents/count")
    suspend fun countAllEvents(): Response<count>

    //delete
    @DELETE("/allevents/{id}")
    suspend fun delEventsById(@Path("id") id: String): Response<Unit>

    //Patch
    @PATCH("/allevents/{id}")
    suspend fun patchEventsById(@Path("id") id: String, @Body allModel: AllModel): Response<Unit>

    //get Events by id
    @GET("/allevents/{id}")
    suspend fun getEventsById(@Path("id") id: String): Response<AllModelOutput>

    //get All Events by event category
    @GET("/alleventsByCategory/{eventcategory}")
    suspend fun getEventsByCategory(@Path("eventcategory") eventcategory: String): Response<List<AllModelOutput>>

    //count Events by event category
    @GET("/allevents/countByCategory/{eventcategory}")
    suspend fun countEventsByCategory(@Path("eventcategory") eventcategory: String): Response<count>

    //post rating
    @POST("/ratings")
    suspend fun postRating(@Body rating: Rating): Response<RatingOutput>

    @GET("/ratings/sum?eventid={eventid}")
    suspend fun averageRating(@Path("eventid") eventid: String): Response<AverageOutput>

    @POST("/check-existence")
    suspend fun checkExistenceRating(@Body existence: Existence): Response<Unit>

    //get rating
    @GET("/ratings")
    suspend fun getRating(): Response<List<RatingOutput>>

    @GET("/ratingsByEvent/{eventid}")
    suspend fun getRatingByEventId(@Path("eventid") eventid: String): Response<List<RatingOutput>>

    @GET("/tutorial")
    suspend fun getTutorial(): Response<List<Tutorial>>

    @GET("/tutorial/search")
    suspend fun searchTutorial(@Query("q") searchQuery: String): Response<List<Tutorial>>

    @POST("/tutorialStatus")
    suspend fun postTutorialStatus(@Body tutorialStatus: TutorialStatus): Response<TutorialStatusOutput>

    @GET("/tutorialByUserId/{tutorialid}/{userid}")
    suspend fun getTutorialByUserId(@Path("tutorialid") tutorialid: String, @Path("userid") userid: String): Response<TutorialStatusOutput>

    @GET("/ranking/sorted-by-score")
    suspend fun getTopLeaderBoards():Response<List<RankingOutput>>

    @POST("/ranking")
    suspend fun postRank(@Body ranking: Ranking): Response<RankingOutput>

    @POST("/check-existence/ranking/{userid}")
    suspend fun checkRanking(@Path("userid") userid: String): Response<Unit>

    @GET("/ranking/by-userid/{userid}")
    suspend fun getIdByuserid(@Path("userid") userid: String): Response<RankingOutput>

    @PATCH("/ranking/{id}")
    suspend fun patchRank(@Path("id") id: String, @Body patchRank: PatchRank): Response<Unit>

    @POST("/favorites")
    suspend fun postFavorites(@Body favorites: Favorites): Response<FavoritesOutput>

    @DELETE("/favorites/{id}")
    suspend fun delFavorites(@Path("id") id: String): Response<Unit>

    @GET("/favoritesByUser/{userid}")
    suspend fun getFavorites(@Path("userid") userid: String): Response<List<FavoritesOutput>>

    @POST("/check-existence")
    suspend fun checkExistenceFavorites(@Body existence: Existence): Response<Boolean>

    @POST("/aboutus")
    suspend fun postAbout(@Body aboutUs: AboutUs): Response<AboutUsOutput>

    @GET("/aboutus/{id}")
    suspend fun getAboutUs(@Path("id") id: String): Response<AboutUsOutput>

    @PATCH("/aboutus/{id}")
    suspend fun patchAboutUs(@Path("id") id: String, @Body aboutUs: AboutUs): Response<AboutUsOutput>

    @POST("/terms")
    suspend fun postTerms(@Body terms: Terms): Response<TermsOutput>

    @GET("/terms/{id}")
    suspend fun getTerms(@Path("id") id: String,): Response<TermsOutput>

    @PATCH("/terms/{id}")
    suspend fun patchTerms(@Path("id") id: String, @Body terms: Terms): Response<TermsOutput>

    @POST("/researcher")
    suspend fun postResearch(@Body researchers: Researchers): Response<ResearchersOutput>

    @GET("/researcher")
    suspend fun getReserachers(): Response<List<ResearchersOutput>>

    @PATCH("/researcher/{id}")
    suspend fun patchResearchers(@Path("id") id: String, @Body researchers: Researchers): Response<ResearchersOutput>

    @DELETE("/researcher/{id}")
    suspend fun deleteResearchers(@Path("id") id: String): Response<Unit>
}