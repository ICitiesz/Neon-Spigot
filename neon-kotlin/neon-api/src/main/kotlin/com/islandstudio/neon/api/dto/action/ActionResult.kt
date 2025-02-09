package com.islandstudio.neon.api.dto.action

import com.islandstudio.neon.shared.core.exception.NeonException

class ActionResult<T> : IActionResult<T> {
    private lateinit var _status: ActionStatus
    private var _result: T? = null
    private var _neonExeption: NeonException? = null

    override val status: ActionStatus
        get() = _status
    override val result: T?
        get() = _result
    override val neonException: NeonException?
        get() = _neonExeption

    fun withStatus(actionStatus: ActionStatus): ActionResult<T> {
        _status = actionStatus

        return this
    }

    fun withSuccessStatus(): ActionResult<T> {
        _status = ActionStatus.SUCCESS

        return this
    }

    fun withFailureStatus(): ActionResult<T> {
        _status = ActionStatus.FAILURE

        return this
    }

    fun withResult(result: T): IActionResult<T>{
        _result = result

        return this
    }

    fun withNeonException(neonException: NeonException): ActionResult<T> {
        _neonExeption = neonException

        return this
    }
}