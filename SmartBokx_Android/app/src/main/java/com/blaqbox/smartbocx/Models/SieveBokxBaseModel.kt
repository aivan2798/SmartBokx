package com.blaqbox.smartbocx.Models

import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
class SieveBokxBaseModel(val text: SieveBokx) {

    fun makeJson(): String {
        return Json.encodeToString(this)
    }
}