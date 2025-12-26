package com.islandstudio.neon.apinew.dto

import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.exception.NeonException
import java.util.*

class ResultProvider<T>(
    val status: ActionResultStatus,
    val result: T? = null,
    val cause: Exception? = null
)  {
    inline fun getResult(
        onSuccess: Optional<T & Any>.() -> Optional<T & Any> = { result?.let { Optional.of(it) } ?: Optional.empty() },
        onFailure: (status: ActionResultStatus, cause: Exception?) -> Unit = { status, cause -> throw NeonException(status.message, cause) }
    ): Optional<T & Any> {
        return if (status is ActionResultStatus.Success) {
            onSuccess(result?.let { Optional.of(it) } ?: Optional.empty())
        } else {
            onFailure(this.status, cause)
            Optional.empty()
        }
    }
}