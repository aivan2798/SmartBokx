package com.blaqbox.smartbocx.Models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class BokxQuery(val content: String,val query: String){

    fun makeJson():String{

        return Json.encodeToString(this)
    }
}