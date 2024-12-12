package com.contexts.calllog

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    lateinit var apiService: ApiService

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun init() = withContext(Dispatchers.IO) {
        // Just a mock server for example purposes fetching data from API
        val mockServer = MockWebServer()
        mockServer.start()
        mockServer.enqueue(MockResponse().setResponseCode(200).setBody(apiResponses))
        Log.d("ApiClient", "MockWebServer started on port: ${mockServer.port}")
        val retrofit = Retrofit.Builder()
            .baseUrl("http://${mockServer.hostName}:${mockServer.port}")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }
}

// Mock API response
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