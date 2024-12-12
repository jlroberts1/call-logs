package com.contexts.calllog

data class CallLogEntry(
    val id: Int,
    val phoneNumber: String,
    val timestamp: Long,
    val callType: String,
)