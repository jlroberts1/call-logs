package com.contexts.calllog

@JvmInline
value class PhoneNumber private constructor(val value: String) {
    companion object {
        // Basic naive phone number validation. Would need to
        // a: put together a more robust solution with more time
        // b: preferably the backend would validate this and ensure that we have valid numbers
        fun from(number: String): Result<PhoneNumber> = runCatching {
            require(number.isNotBlank()) { "Phone number cannot be blank" }
            require(number.all { it.isDigit() || it in setOf('+', '-', '(', ')', ' ') }) {
                "Phone number contains invalid characters"
            }
            PhoneNumber(number)
        }
    }

    fun toFormattedString(): String {
        return value
    }
}