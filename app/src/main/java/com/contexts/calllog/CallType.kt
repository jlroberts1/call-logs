package com.contexts.calllog

import android.provider.CallLog

enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED,
    VOICEMAIL,
    REJECTED,
    BLOCKED,
    ANSWERED_EXTERNALLY,
    UNKNOWN;

    companion object {
        fun fromLocal(type: Int) = when (type) {
            CallLog.Calls.OUTGOING_TYPE -> OUTGOING
            CallLog.Calls.INCOMING_TYPE -> INCOMING
            CallLog.Calls.MISSED_TYPE -> MISSED
            CallLog.Calls.VOICEMAIL_TYPE -> VOICEMAIL
            CallLog.Calls.REJECTED_TYPE -> REJECTED
            CallLog.Calls.BLOCKED_TYPE -> BLOCKED
            CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> ANSWERED_EXTERNALLY
            else -> UNKNOWN
        }

        fun fromRemote(callType: String) = try {
            valueOf(callType.uppercase())
        } catch (e: IllegalArgumentException) {
            UNKNOWN
        }
    }
}