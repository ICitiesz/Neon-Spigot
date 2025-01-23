package com.islandstudio.neon.api.dto.action

import com.islandstudio.neon.shared.core.exception.NeonException

data class ActionResult<T>(
    override val result: T? = null,
    override val status: ActionStatus,
    override val displayMessage: String? = null,
    override val logMessage: String? = null,
    override val neonException: NeonException? = null,
) : IActionResult<T> {

}