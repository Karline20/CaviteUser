package coding.legaspi.caviteuser.presentation.di.events

import coding.legaspi.caviteuser.presentation.SplashActivity
import coding.legaspi.caviteuser.presentation.about.AboutActivity
import coding.legaspi.caviteuser.presentation.auth.LoginActivity
import coding.legaspi.caviteuser.presentation.auth.RegistrationActivity
import coding.legaspi.caviteuser.presentation.auth.profilecreation.ProfileCreation
import coding.legaspi.caviteuser.presentation.di.events.EventModule
import coding.legaspi.caviteuser.presentation.favorites.FavoritesActivity
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.home.event.ViewEventActivity
import coding.legaspi.caviteuser.presentation.home.rating.RatingActivity
import coding.legaspi.caviteuser.presentation.home.rvevent.RvEventActivity
import coding.legaspi.caviteuser.presentation.itinerary.ItineraryActivity
import coding.legaspi.caviteuser.presentation.menu.MenuActivity
import coding.legaspi.caviteuser.presentation.play.PlayActivity
import coding.legaspi.caviteuser.presentation.play.ranking.LeaderBoardsActivity
import coding.legaspi.caviteuser.presentation.terms.TermsActivity
import coding.legaspi.caviteuser.presentation.tutorial.TutorialActivity
import coding.legaspi.caviteuser.presentation.tutorial.tutor.TutorActivity
import dagger.Subcomponent

@EventScope
@Subcomponent(
    modules = [
        EventModule::class
    ]
)
interface EventSubComponent {
    fun inject(viewEventActivity: ViewEventActivity)
    fun inject(aboutActivity: AboutActivity)
    fun inject(homeActivity: HomeActivity)
    fun inject(ratingActivity: RatingActivity)
    fun inject(tutorialActivity: TutorialActivity)
    fun inject(tutorActivity: TutorActivity)
    fun inject(playActivity: PlayActivity)
    fun inject(menuActivity: MenuActivity)
    fun inject(splashActivity: SplashActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(profileCreation: ProfileCreation)
    fun inject(registrationActivity: RegistrationActivity)
    fun inject(leaderBoardsActivity: LeaderBoardsActivity)
    fun inject(favoritesActivity: FavoritesActivity)
    fun inject(termsActivity: TermsActivity)
    fun inject(rvEventActivity: RvEventActivity)
    fun inject(itineraryActivity: ItineraryActivity)
    @Subcomponent.Factory
    interface Factory{
        fun create():EventSubComponent
    }
}