package com.contexts.calllog

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    // TODO: Type errors
    data class Error(val message: String, val cause: Throwable? = null) : ApiResult<Nothing>()
}