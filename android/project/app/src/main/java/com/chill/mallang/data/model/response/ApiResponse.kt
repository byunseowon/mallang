package com.chill.mallang.data.model.response

sealed class ApiResponse<out T : Any?> {
    data class Success<out T : Any?>(
        val body: T?,
    ) : ApiResponse<T>()

    data class Error(
        val errorCode: Int = 0,
        val errorMessage: String = "",
    ) : ApiResponse<Nothing>()

    data object Init : ApiResponse<Nothing>()
}
