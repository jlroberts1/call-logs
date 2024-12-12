package com.contexts.calllog

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("call_logs")
    fun getCallLogs(): Call<List<CallLogEntry>>
}