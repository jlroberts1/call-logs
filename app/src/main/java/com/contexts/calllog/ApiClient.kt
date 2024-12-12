package com.contexts.calllog

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val mockServer = MockWebServer()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://${mockServer.hostName}:${mockServer.port}")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    private fun initServer() {
        mockServer.enqueue(MockResponse().setResponseCode(200).setBody(apiResponses))
    }

    fun startMockServer() {
        try {
            Log.d("ApiClient", "MockWebServer started on port: ${mockServer.port}")
            initServer()
        } catch (e: IllegalStateException) {
            // Server is already running
            Log.d("ApiClient", "Server already running on port: ${mockServer.port}")
        }
    }
}

private val apiResponses = """
    [
        {
            "id": 1,
            "phoneNumber": "740-555-1212",
            "timestamp": 1733932800000,
            "callType": "Missed"
        },
        {
            "id": 2,
            "phoneNumber": "740-547-1234",
            "timestamp": 1733932800000,
            "callType": "Received"
        }
    ]
""".trimIndent()