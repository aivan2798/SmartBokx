package com.blaqbox.smartbocx.Models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
class BokxCredits(val bokx_requests: Int, val bokx_request_limit: Int, val bokx_responses: Int, val bokx_responses_limit: Int) {

}