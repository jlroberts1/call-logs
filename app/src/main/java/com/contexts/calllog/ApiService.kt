package com.contexts.calllog

import retrofit2.Response
import retrofit2.http.GET
import java.time.Instant

interface ApiService {
    @GET("call_logs")
    suspend fun getCallLogs(): Response<List<ApiCallLogResponse>>
}

// Maintain a model specific to API requirements as there may be additonal metadata that isn't
// available for local calls
data class ApiCallLogResponse(
    val id: Int?,
    val phoneNumber: String?,
    val timestamp: Long?,
    val callType: String?
)

fun ApiCallLogResponse.toDomainModel(): ApiResult<CallLogEntry> = runCatching {

    // Fail here instead of having an ambiguous Serialization error
    // If I remember correctly Gson has some issues if properties are missing that are required
    requireNotNull(id) { "Call log ID cannot be null" }
    requireNotNull(timestamp) { "Timestamp cannot be null" }
    requireNotNull(phoneNumber) { "Phone number cannot be null" }
    requireNotNull(callType) { "Call type cannot be null" }

    val validatedPhone = PhoneNumber.from(phoneNumber).getOrElse {
        throw IllegalArgumentException("Invalid phone number")
    }

    val validatedType = CallType.fromRemote(callType)

    CallLogEntry(
        // TODO: These id's could clash with the local ID's if there is not offset built in to the
        // remote ids an offset or identifier should be added here
        id = id,
        phoneNumber = validatedPhone,
        timestamp = Instant.ofEpochMilli(timestamp),
        callType = validatedType,
        Origin.REMOTE
    )
}.fold(
    onSuccess = { ApiResult.Success(it) },
    // TODO: Type errors
    onFailure = { ApiResult.Error("Failed to parse call log: ${it.message}", it)}
)