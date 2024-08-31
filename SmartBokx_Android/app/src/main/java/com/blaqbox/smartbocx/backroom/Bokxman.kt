package com.blaqbox.smartbocx.backroom

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import androidx.lifecycle.lifecycleScope
import com.blaqbox.smartbocx.Models.BokxCredits
import com.blaqbox.smartbocx.ui.BokxBot
import io.github.jan.supabase.SupabaseClientBuilder
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.SessionSource
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.RealtimeChannelBuilder
import io.github.jan.supabase.realtime.RealtimeRateLimitException
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import io.ktor.util.Identity.decode
import io.ktor.utils.io.printStack
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive


public class Bokxman(sb_key: String, sb_url: String,val auth_status: MutableLiveData<Boolean>){
    lateinit var supa_client: SupabaseClient
    lateinit var auth_status_in: MutableLiveData<Boolean>
    init {

        supa_client = createSupabaseClient(sb_url, sb_key) {
            install(Auth)
            install(Realtime)
            install(Postgrest)
            useHTTPS = true
        }
        //supa_client.channel("supa_client")
        //Log.i("subscriptions channel",channel.topic)
        //var channel = supa_client.realtime.channel("my-channel")
            /*
            .on("postgres_changes", {
                    event, payload ->
                when (event) {
                    "INSERT" -> {
                        // Handle new record
                        println("New record: $payload")
                    }
                    "UPDATE" -> {
                        // Handle updated record
                    }
                    "DELETE" -> {
                        // Handle deleted record
                    }
                }
            })
        */

        //Log.i("Channel List",subscriptions)
        GlobalScope.launch {

            //supa_client.realtime.connect()
            //Log.i("started listener","started listening")
            /*channel.subscribe(true)
            Log.i("finished subscriptions","OK")
            val subscriptions = supa_client.realtime.subscriptions.entries
            Log.i("subscriptions account",subscriptions.size.toString())
            subscriptions.forEach{
                Log.i("subscriptions key: ",it.key)
                Log.i("channel_topic",it.value.topic)
            }*/
            //supa_client.realtime.
            supa_client.auth.sessionStatus.collect {
                when(it) {
                    is SessionStatus.Authenticated -> {
                        println("Received new authenticated session.")
                        when(it.source) { //Check the source of the session
                            SessionSource.External -> {
                                Log.i("kotlin log","external signal")
                            }
                            is SessionSource.Refresh -> {
                                Log.i("kotlin log","refresh signal")
                            }
                            is SessionSource.SignIn -> {
                                auth_status.postValue(true)
                                Log.i("kotlin log","signin signal")
                            }
                            is SessionSource.SignUp -> {
                                Log.i("kotlin log","signup signal")
                            }
                            SessionSource.Storage -> {
                                Log.i("kotlin log","storage signal")
                            }
                            SessionSource.Unknown -> {
                                Log.i("kotlin log","unknown signal")
                            }
                            is SessionSource.UserChanged -> {
                                Log.i("kotlin log","user changed signal")
                            }
                            is SessionSource.UserIdentitiesChanged -> {
                                Log.i("kotlin log","user id changed signal")
                            }


                            else->{
                                print("dummy")
                            }
                        }

                    }
                    SessionStatus.LoadingFromStorage -> println("Loading from storage")
                    SessionStatus.NetworkError -> println("Network error")
                    is SessionStatus.NotAuthenticated -> {
                        if(it.isSignOut) {
                            auth_status.postValue(false)
                            println("User signed out")
                        } else {
                            println("User not signed in")
                        }
                    }
                }
            }


        }
    }
    fun getUserSession():UserSession?{

        var user_session = supa_client.auth.currentSessionOrNull()
        Log.i("user session","regetting user session "+user_session)

        return user_session
    }

    fun reauth(){
        Log.i("REAUTH","Reauthing")
        /*GlobalScope.launch{
            supa_client.auth.refreshCurrentSession()

            }*/
    }

    fun getUserCredits(bokx_credits: MutableLiveData<BokxCredits>){
        GlobalScope.launch {
            try {
                var db_result = supa_client.from("Bokx_Metrics").select(
                    Columns.list(
                        "bokx_requests",
                        "bokx_request_limit",
                        "bokx_responses",
                        "bokx_responses_limit"
                    )
                ).decodeSingle<BokxCredits>()
                //DataConnector.getInstance().setCredits(db_result)
                Log.i("user credits are: ", db_result.bokx_request_limit.toString())
                bokx_credits.postValue(db_result)
            }
            catch (except: Exception){
                Log.i("CREDITS ERROR","user credits are not here ")
            }

        }

    }

    fun loginUser(sb_context: Context,user_email: String, user_password: String,auth_status_bot: MutableLiveData<String>,has_auth: MutableLiveData<Boolean>){

        GlobalScope.launch {
            lateinit var result: Unit
            try {
                    result = supa_client.auth.signInWith(Email, "www.google.com") {
                    email = user_email
                    password = user_password
                }
                has_auth.postValue(true)
                Log.i("signin data: ",result.toString())
            }
            catch (except: RestException){

                var status_code = except.error
                var description = except.description
                auth_status_bot.postValue("signin error: "+description)
                Log.i("signin exception: ",status_code+" : "+description)
                except.printStack()
            }

            catch (except: Exception){
                auth_status_bot.postValue("internal error: ")
                Log.i("signup error: ", except.toString())
            }
        }

    }

    fun getLoginStatus():Boolean{
        val session_stat: SessionStatus =  supa_client.auth.sessionStatus.value
        if (session_stat is SessionStatus.Authenticated){
            //auth_status.postValue(true)
            return true
        }
        //auth_status.postValue(false)
        return false
    }

    fun logoutUser(){

        GlobalScope.launch {
            supa_client.auth.signOut()
        }

    }

    fun createUser(sb_context: Context,user_email: String, user_password: String,auth_status_bot: MutableLiveData<String>){

        GlobalScope.launch{
            try {
                var result = supa_client.auth.signUpWith(Email, "www.google.com") {
                    email = user_email
                    password = user_password
                }
                if (result != null) {
                    Log.i("signup result", result.toString())
                    val user_metadata = result.userMetadata
                    if (user_metadata != null) {
                        val verification_result = user_metadata["email_verified"]
                        if (verification_result != null) {
                            if (verification_result.jsonPrimitive.boolean == false) {

                                auth_status_bot.postValue("Please Verify your Email And Login")
                            }
                        } else {

                        }
                    }

                } else {
                    Log.i("signup result", "signup failed")
                }
            }
             catch(except: RestException){
                    Log.i("signup error: ", except.toString())
                    var status_code = except.error
                    var description = except.description
                    auth_status_bot.postValue("signin error: "+description)
                    Log.i("signup exception: ",status_code+" : "+description)
                }
            catch (except: Exception){

                Log.i("signup error: ", except.toString())
                auth_status_bot.postValue("internal error: ")
            }
            }
        }
    }
