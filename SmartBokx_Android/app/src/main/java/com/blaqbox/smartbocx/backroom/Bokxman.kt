package com.blaqbox.smartbocx.backroom

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blaqbox.smartbocx.Models.BokxCredits
import com.blaqbox.smartbocx.db.NoteQA
import com.blaqbox.smartbocx.utils.Constants
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionSource
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.ktor.utils.io.printStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Date


public class Bokxman(context: Context , sb_key: String, sb_url: String,val auth_status: MutableLiveData<Boolean>){
    lateinit var supa_client: SupabaseClient
    lateinit var dataConnector: DataConnector
    lateinit var auth_status_in: MutableLiveData<Boolean>
    lateinit var shared_prefs: SharedPreferences
    lateinit var channel_name: String
    lateinit var active_channel: RealtimeChannel
    init {

        supa_client = createSupabaseClient(sb_url, sb_key) {
            install(Auth)
            install(Realtime)
            install(Postgrest)
            
            useHTTPS = true
        }
        shared_prefs = context.getSharedPreferences("AUTH",Context.MODE_PRIVATE)
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



                        with (shared_prefs.edit()){

                        putBoolean(Constants.AUTH_STATUS,true)
                        apply()}
                        when(it.source) { //Check the source of the session
                            SessionSource.External -> {
                                Log.i("kotlin log","external signal")
                                with(shared_prefs.edit()){
                                    putBoolean(Constants.EXTERNAL_SESSION,true)
                                    apply()
                                }
                            }
                            is SessionSource.Refresh -> {
                                Log.i("kotlin log","refresh signal")
                                shared_prefs.edit().putBoolean(Constants.REFRESH_SESSION,true)
                            }
                            is SessionSource.SignIn -> {
                                auth_status.postValue(true)
                                Log.i("kotlin log","signin signal")
                            }
                            is SessionSource.SignUp -> {
                                Log.i("kotlin log","signup signal")
                                //shared_prefs.edit().putBoolean("SIGNUP",true)
                            }
                            SessionSource.Storage -> {
                                Log.i("kotlin log","storage signal")
                                with(shared_prefs.edit()){
                                    putBoolean(Constants.STORAGE_SESSION,true)
                                    apply()
                                }
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
                    SessionStatus.NetworkError ->{println("Network error")
                        with (shared_prefs.edit()){


                            putBoolean(com.blaqbox.smartbocx.utils.Constants.NETWORK_ERROR,true)
                            apply()}
                    }

                    is SessionStatus.NotAuthenticated -> {
                        with (shared_prefs.edit()){

                            putBoolean(Constants.AUTH_STATUS,true)
                            apply()}
                        if(it.isSignOut) {
                            auth_status.postValue(false)
                            println("User signed out")
                        } else {
                            with (shared_prefs.edit()){


                                putBoolean(com.blaqbox.smartbocx.utils.Constants.SIGNED_OUT,true)
                                apply()}
                            println("User not signed in")
                        }
                    }
                }
            }


        }
    }

    fun initBokxman(){
        dataConnector = DataConnector.getInstance()
    }
    fun getUserSession():UserSession?{
        /*
        var session_fin: Boolean = false
        GlobalScope.launch {
             //supa_client.auth.refreshCurrentSession()
            supa_client.auth.reauthenticate();
            session_fin = true
            Log.i("Session Update: ","session finished: "+session_fin)
        }
        while(session_fin == false)
        {
            //Log.i("SESSION UPDATE: ","fixing session")
        }
        */


        var user_session = supa_client.auth.currentSessionOrNull();
        Log.i("Session info", "expires in: "+user_session?.expiresIn)
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
                auth_status_bot.postValue("signin error: "+status_code)
                has_auth.postValue(true)
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

    fun setChannelName(channel_to_use:String){
        channel_name = channel_to_use
    }

    fun logoutUser(){

        GlobalScope.launch {
            supa_client.auth.signOut()
        }

    }

    fun handleEvent(){

    }

    fun openChannel(){
        Log.i("using channel: ",channel_name)
        active_channel = supa_client.channel(channel_name)

        /*
        val broadcastFlow = active_channel.broadcastFlow<JsonObject>(event = "siever")
// Collect the flow
        broadcastFlow.onEach { // it: Message
            println(it)
        }.launchIn(handleEvent) // launch a new coroutine to collect the flow
        active_channel.subscribe(blockUntilSubscribed = true)
        */
        //runBlocking
       /* GlobalScope.launch{
            Log.i("channel update: ","waiting for broadcast")
            active_channel.subscribe(blockUntilSubscribed = true)
            Log.i("channel update: ","subscribe finished")
            active_channel.broadcastFlow<JsonObject>("siever")
                .collect { message ->
                    println("Received broadcast: ${message}")
                }

        }*/

        GlobalScope.launch {
            active_channel.subscribe(blockUntilSubscribed = true)
            Log.i("finished subscribing", "okay")

            active_channel.broadcastFlow<JsonObject>("siever").collect {
                println(it["message"])
                var bc_msg = it["message"]
                /*withContext(Dispatchers.Main){
                    DataConnector.getAllAIResults().clear();
                    DataConnector.getAllAIResults().add(NoteQA("your answer",bc_msg.toString()))
                    DataConnector.getNoteQAAdapter().notifyDataSetChanged()
                }*/

                Log.i("new channel mg", it.toString())
            }
        }

        GlobalScope.launch {

            active_channel.broadcastFlow<JsonObject>("bokx_qa").collect {
                println(it["message"])
                var arr_bc_msg = it["message"]?.jsonArray
                var obj_bc_msg = if (arr_bc_msg == null) null else arr_bc_msg.get(0).jsonObject
                var bc_msg: String = if (obj_bc_msg == null) "No Answer" else obj_bc_msg.get("Value").toString()
                withContext(Dispatchers.Main){

                    var note_qa: NoteQA = DataConnector.getAllAIResults().get(0)
                    DataConnector.getAllAIResults().clear();
                    note_qa.note_answer = bc_msg.removeSurrounding("\"")
                    DataConnector.getAllAIResults().add(note_qa)
                    DataConnector.getNoteQAAdapter().notifyDataSetChanged()
                }

                Log.i("new channel mg",it.toString())
            }




        }

        GlobalScope.launch {

            active_channel.broadcastFlow<JsonObject>("all_notes").collect {
                println(it["message"])
                //[[{"Key":"content","Value":"mt v2 download"},{"Key":"link","Value":"https://mt2.cn/download/"},{"Key":"title","Value":"LINK_NOTE_1743452957198"},{"Key":"id","Value":"0"}]]
                var arr_bc_msg = it["message"]?.jsonArray

                arr_bc_msg?.forEach {
                    var my_dic = HashMap<String,String>()
                    it.jsonArray?.forEach {datum->
                        var key = datum.jsonObject["Key"].toString().removeSurrounding("\"")
                        var content = datum.jsonObject["Value"].toString().removeSurrounding("\"")
                        my_dic.put(key,content)
                        println(key.toString() + ":" + content)
                        println(key + ":" + content)
                    }


                    //var str_id = if (my_dic["id"]==null) "0" else my_dic["id"]
                    //var id: Int = if (str_id!=null) str_id.toInt() else 0
                    //var temp_note = Note(id,my_dic["title"],my_dic["link"],my_dic["content"])
                    dataConnector.addNewNotePlain(my_dic["title"],my_dic["link"],my_dic["content"])
                }
                //var a_bc_msg = if (arr_bc_msg == null) null else arr_bc_msg.get(0).jsonArray

                //var bc_msg: String = if (obj_bc_msg == null) "No Answer" else obj_bc_msg.get("Value").toString()
                withContext(Dispatchers.Main){
                    dataConnector.syncStatus.postValue(true)
                    dataConnector.refresh()
                    //DataConnector.getInstance().addNewNote()
                    //var note_qa: NoteQA = DataConnector.getAllAIResults().get(0)
                    //DataConnector.getAllAIResults().clear();
                    //note_qa.note_answer = bc_msg.removeSurrounding("\"")
                    //DataConnector.getAllAIResults().add(note_qa)
                    //DataConnector.getNoteQAAdapter().notifyDataSetChanged()
                }

                Log.i("new channel mg",it.toString())
            }




        }

        GlobalScope.launch {

            active_channel.broadcastFlow<JsonObject>("bokx_gen").collect {
                println(it["message"])
                //message:[{"Key":"target","Value":"https://www.youtube.com/shorts/gVAFSXGuLM0?app=desktop"},{"Key":"message","Value":"The video features a still image of Pain, a character from the anime series Naruto, in a dark-colored robe with the Akatsuki symbol on the back. He stands in front of a destroyed city with rain falling. There are text overlays appearing and disappearing throughout the video. The first one reads \"The most painful thing\". The second text overlay then says \"isn't a cut\", followed by \"or a broken nose\", and finally \"the most painful thing is seeing the people you made memories with slowly become memories\". The video seems to be trying to express the emotional pain of losing loved ones and watching them fade away."}]
                var arr_bc_msg = it["message"]?.jsonArray
                var my_dic = HashMap<String,String>()
                var status_code = 0
                lateinit var json_content : JsonElement
                arr_bc_msg?.forEach { datum->
                        var key = datum.jsonObject["Key"].toString().removeSurrounding("\"")
                        var content = datum.jsonObject["Value"]
                        if(key=="status_code"){
                            status_code = if (content!=null) content.jsonPrimitive.int else 0
                        }
                        if(key.equals("message")) {
                            if (content!=null){
                                json_content = content
                            }


                        }
                }
                if (status_code==200){
                    if(json_content!=null){
                        json_content.jsonArray.forEach { datumx->
                            var xkey = datumx.jsonObject["Key"].toString().removeSurrounding("\"")
                            var xcontent = datumx.jsonObject["Value"].toString().removeSurrounding("\"")
                            my_dic.put(xkey,xcontent)
                            println(xkey.toString() + ":" + xcontent)
                            println(xkey + ":" + xcontent)
                        }
                    }

                }


                    //var str_id = if (my_dic["id"]==null) "0" else my_dic["id"]
                    //var id: Int = if (str_id!=null) str_id.toInt() else 0
                    //var temp_note = Note(id,my_dic["title"],my_dic["link"],my_dic["content"])
                    var epoch_sec = Date().time
                    val note_name = "LINK_NOTE_$epoch_sec"
                    Log.i("AUTOGEN_NAME",note_name)
                    //dataConnector.addNewNotePlain(note_name,my_dic["target"],my_dic["message"])

                val auto_intent = Intent(dataConnector.dbContext, AutoNoteReceiver::class.java)
                auto_intent.setAction("com.blaqbokx.smartbocx.AUTO_NOTE.content")
                auto_intent.putExtra("MAIN_NOTE", my_dic["message"])
                auto_intent.putExtra("TARGET",my_dic["target"])
                auto_intent.putExtra("STATUS",status_code)
                dataConnector.dbContext.sendBroadcast(auto_intent)
                }
                //var a_bc_msg = if (arr_bc_msg == null) null else arr_bc_msg.get(0).jsonArray

                //var bc_msg: String = if (obj_bc_msg == null) "No Answer" else obj_bc_msg.get("Value").toString()
                /*withContext(Dispatchers.Main){
                    dataConnector.syncStatus.postValue(true)
                    dataConnector.refresh()
                    //DataConnector.getInstance().addNewNote()
                    //var note_qa: NoteQA = DataConnector.getAllAIResults().get(0)
                    //DataConnector.getAllAIResults().clear();
                    //note_qa.note_answer = bc_msg.removeSurrounding("\"")
                    //DataConnector.getAllAIResults().add(note_qa)
                    //DataConnector.getNoteQAAdapter().notifyDataSetChanged()
                }
                */








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
                            auth_status_bot.postValue("User already verified, Please login")
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
