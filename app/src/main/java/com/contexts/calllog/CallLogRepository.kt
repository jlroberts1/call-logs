package com.contexts.calllog

import android.content.Context
import android.provider.CallLog
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class CallLogRepository(
    private val context: Context,
    private val apiService: ApiService
) {
    suspend fun getCombinedCallLogs(): ApiResult<List<CallLogEntry>> = withContext(Dispatchers.IO) {
        try {
            val phoneLogs = getCallLogsFromPhone()
            when (val apiLogs = getApiCallLogs()) {
                is ApiResult.Success -> {
                    val combinedLogs = (apiLogs.data + phoneLogs)
                        .sortedByDescending { it.timestamp }
                    ApiResult.Success(combinedLogs)
                }

                is ApiResult.Error -> {
                    Log.w("CallLogRepository", "Failed to fetch API logs: ${apiLogs.message}")
                    ApiResult.Success(emptyList())
                }
            }
        } catch (e: Exception) {
            ApiResult.Error("Failed to fetch call logs", e)
        }
    }

    private suspend fun getApiCallLogs(): ApiResult<List<CallLogEntry>> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("CallLogRepository", "Making api call")
                val response = apiService.getCallLogs()
                if (!response.isSuccessful) {
                    return@withContext ApiResult.Error("API call failed: ${response.code()}")
                }

                val apiLogs =
                    response.body() ?: return@withContext ApiResult.Error("Empty response from API")

                val validatedLogs = apiLogs.mapNotNull { log ->
                    when (val result = log.toDomainModel()) {
                        is ApiResult.Success -> result.data
                        is ApiResult.Error -> {
                            Log.w(
                                "CallLogRepository",
                                "Skipping invalid log entry: ${result.message}"
                            )
                            null
                        }
                    }
                }
                ApiResult.Success(validatedLogs)
            } catch (e: Exception) {
                ApiResult.Error("Failed to fetch API Logs")
            }
        }

    private suspend fun getCallLogsFromPhone(): List<CallLogEntry> = withContext(Dispatchers.IO) {
        val callLogs = mutableListOf<CallLogEntry>()
        try {
            Log.d("CallLogRepository", "Fetching local call logs")
            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls._ID,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.DATE,
                    CallLog.Calls.TYPE,
                ),
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls._ID))
                        val number =
                            cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                        val date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))
                        val type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                        val phoneNumber = PhoneNumber.from(number).getOrNull() ?: continue
                        callLogs.add(
                            CallLogEntry(
                                id.toInt(),
                                phoneNumber,
                                Instant.ofEpochMilli(date),
                                CallType.fromLocal(type),
                                Origin.LOCAL
                            )
                        )
                    } catch (e: Exception) {
                        Log.w("CallLogRepository", "Failed to get local call log entry", e)
                        continue
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CallLogRepository", "Failed to query call logs", e)
        }
        callLogs
    }
}
