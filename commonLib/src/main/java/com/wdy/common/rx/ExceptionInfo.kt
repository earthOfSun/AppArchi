package com.wdy.common.rx

data class ExceptionInfo(
        val timestamp: Long,
        val status: Int,
        val error: String,
        val exception: String,
        val message: String,
        val path: String
)