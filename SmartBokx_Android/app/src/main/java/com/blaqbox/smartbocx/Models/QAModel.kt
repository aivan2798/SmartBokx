package com.blaqbox.smartbocx.Models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
data class QAModel(val content: String,val route: String, val user_id: String){


}