package com.blaqbox.smartbocx.Models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject

@Serializable
data class SieveModel(val function: String, val inputs: SieveBaseModel){

    fun makeJson():String{

        return Json.encodeToString(this)

    }

}