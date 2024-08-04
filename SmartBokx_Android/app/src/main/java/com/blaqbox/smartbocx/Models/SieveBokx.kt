package com.blaqbox.smartbocx.Models

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable

@Serializable
data class SieveBokx(val content: List<NoteJson>,val route: String, val user_id: String){

    fun makeJson():String{

        return Json.encodeToString(this)
    }
}