package com.contexts.calllog

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CallLogApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var repository: CallLogRepository
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            // This is initialized here for the mockserver to be started before the activity
            ApiClient.init()
            // Use dependency injection in real life
            repository = CallLogRepository(applicationContext, ApiClient.apiService)
        }
    }
}