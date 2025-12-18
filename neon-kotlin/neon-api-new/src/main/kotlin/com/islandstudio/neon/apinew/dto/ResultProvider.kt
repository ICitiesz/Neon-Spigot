package com.islandstudio.neon.apinew.dto

import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.exception.NeonException
import java.util.*

class ResultProvider<T>(
    private val status: ActionResultStatus,
    private val result: T? = null,
    private val cause: Exception? = null
)  {
    fun getResult(
        onSuccess: (result: T?) -> Optional<T & Any> = { result?.let { Optional.of(it) } ?: Optional.empty() },
        onFailure: (status: ActionResultStatus, cause: Exception?) -> Unit = { status, cause -> throw NeonException(status.message, cause) }
    ): Optional<T & Any> {
        return if (status is ActionResultStatus.Success) {
            onSuccess(result)
        } else {
            onFailure(this.status, cause)
            Optional.empty()
        }
    }
}