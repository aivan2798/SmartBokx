package com.blaqbox.smartbocx.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


class JsonTool{

    init {

    }

    fun decodeJson(json_str: String): Map<String,JsonElement>{
        return Json.parseToJsonElement(json_str).jsonObject.toMap();
    }
}