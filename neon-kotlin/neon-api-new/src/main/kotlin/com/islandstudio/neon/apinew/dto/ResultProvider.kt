package com.islandstudio.neon.apinew.dto

import com.islandstudio.neon.apinew.status.ActionResultStatus
import java.util.*

class ResultProvider<T>(
    private val status: ActionResultStatus,
    private val result: T? = null
)  {
    fun onResultReceived(onSuccess: (result: Optional<T & Any>) -> Unit = {}, onFailure: (status: ActionResultStatus) -> Unit = {}): Result<ActionResultStatus.Success> {
        if (status is ActionResultStatus.Success) {
            onSuccess(result?.let { Optional.of(it) } ?: Optional.empty())
            return Result.success(ActionResultStatus.Success())
        }

        onFailure(this.status)

        return Result.failure(Throwable("Failed to receive result"))
    }
}