package com.blaqbox.smartbocx.Models


import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable

@Serializable
data class NoteJson(val note_title: String,val note_link: String,val note_description: String) {

    fun makeJson():String{

        return Json.encodeToString(this)
    }
}