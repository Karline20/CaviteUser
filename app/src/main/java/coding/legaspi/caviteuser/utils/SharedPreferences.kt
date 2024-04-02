package coding.legaspi.caviteuser.utils

import android.content.Context
import android.content.SharedPreferences
import coding.legaspi.caviteuser.BuildConfig

class SharedPreferences {

    private lateinit var sharedPreferences: SharedPreferences

    fun saveCreation(context: Context, isFinished: String){
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("isFinished", isFinished).apply()
    }

    fun checkCreation(context: Context): String{
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val isFinished = sharedPreferences.getString("isFinished", null)
        return isFinished.toString()
    }

    fun saveTerms(context: Context, firstTime: String){
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("firstTime", firstTime).apply()
    }

    fun checkTerms(context: Context): String{
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val firstTime = sharedPreferences.getString("firstTime", null)
        return firstTime.toString()
    }

    fun saveToken(context: Context, token: String, userid: String){
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("token", token).apply()
        sharedPreferences.edit().putString("userid", userid).apply()
    }

    fun checkToken(context: Context): Pair<String?, String?> {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userid = sharedPreferences.getString("userid", null)
        return Pair(token, userid)
    }

    fun deleteToken(context:Context){
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("token").apply()
        sharedPreferences.edit().remove("userid").apply()
    }

}