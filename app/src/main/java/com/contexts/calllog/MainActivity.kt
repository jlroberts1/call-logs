package com.contexts.calllog

import android.os.Bundle
import android.provider.CallLog
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import okhttp3.mockwebserver.MockWebServer

class CallLogActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CallLogAdapter
    private lateinit var mockWebServer: MockWebServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call_log)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerView)
        adapter = CallLogAdapter()
        recyclerView.adapter = adapter

        fetchCallLogs()
    }

    override fun onDestroy() {
        super.onDestroy()
        mockWebServer.shutdown()
    }

    private fun fetchCallLogs() {
        try {
            ApiClient.startMockServer()


            val callLogsFromPhone = getCallLogsFromPhone()

            val apiCall = ApiClient.apiService.getCallLogs()
            val response = apiCall.execute()

            if (response.isSuccessful) {
                val callLogsFromApi = response.body() ?: emptyList()
                val combinedLogs = callLogsFromPhone + callLogsFromApi

                adapter.submitList(combinedLogs)
            } else {
                // Handle failed response
                showError("Failed to fetch call logs: ${response.message()}")
            }
        } catch (e: Exception) {
            // Handle general network errors
            showError("Error fetching call logs: ${e.message}")
        }
    }

    private fun getCallLogsFromPhone(): List<CallLogEntry> {
        val callLogs = mutableListOf<CallLogEntry>()

        val cursor = contentResolver.query(
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
        )

        Thread.sleep(3000)

        cursor?.apply {
            while (moveToNext()) {
                val id = getString(getColumnIndex(CallLog.Calls._ID))
                val number = getString(getColumnIndex(CallLog.Calls.NUMBER))
                val date = getLong(getColumnIndex(CallLog.Calls.DATE))
                val type = getInt(getColumnIndex(CallLog.Calls.TYPE))

                callLogs.add(
                    CallLogEntry(
                        id.toInt(),
                        number,
                        date,
                        getCallType(type)
                    )
                )
            }
            close()
        }
        return callLogs
    }

    private fun getCallType(type: Int): String {
        return when (type) {
            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
            CallLog.Calls.INCOMING_TYPE -> "Incoming"
            CallLog.Calls.MISSED_TYPE -> "Missed"
            else -> "Unknown"
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}