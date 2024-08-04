package com.blaqbox.smartbocx.Models
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class SieveBaseModel(val text: QAModel) {

    fun makeJson():String{
        return Json.encodeToString(this)
    }
}