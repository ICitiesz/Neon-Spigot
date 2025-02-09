package com.islandstudio.neon.api.dto.action

import com.islandstudio.neon.shared.core.exception.NeonException

interface IActionResult<T> {
    val result: T?
    val status: ActionStatus
    val neonException: NeonException?

    fun onSuccess(block: (actionResult: IActionResult<T>) -> Unit = {} ): IActionResult<T> {
        if (status == ActionStatus.SUCCESS) {
            block(this)
        }

        return this
    }

    fun onFailure(block: (actionResult: IActionResult<T>) -> Unit = {}): IActionResult<T> {
        if (status == ActionStatus.FAILURE) {
            block(this)
        }

        return this
    }

    fun onOtherStatus(block: (actionResult: IActionResult<T>) -> Unit = {}): IActionResult<T> {
        if (!(status == ActionStatus.SUCCESS || status == ActionStatus.FAILURE)) {
            block(this)
        }

        return this
    }
}