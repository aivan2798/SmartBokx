package com.blaqbox.smartbocx.Models
import com.blaqbox.smartbocx.Models.QAModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
class  QABuilder(val xcontent: String,val xroute: String, val xuser_id: String){
    lateinit var qaModel: QAModel
    init {
        qaModel = QAModel(xcontent, xroute, xuser_id)
    }

    fun makeJson():String{
        return Json.encodeToString(qaModel)
    }

    fun getQAModel():QAModel{
        return qaModel
    }
}