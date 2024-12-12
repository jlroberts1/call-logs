package com.contexts.calllog

import java.time.Instant

// Add type for PhoneNumber, Origin, and CallType so that we can rely on these types elsewhere
// and do validations instead of passing down an object of strings
data class CallLogEntry(
    val id: Int,
    val phoneNumber: PhoneNumber,
    val timestamp: Instant,
    val callType: CallType,
    val origin: Origin
)

enum class Origin {
    REMOTE,
    LOCAL;

    companion object {
        fun Origin.toDisplayText(): String = when (this) {
            REMOTE -> "Remote"
            LOCAL -> "Local"
        }
    }
}