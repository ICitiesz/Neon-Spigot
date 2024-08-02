package com.islandstudio.neon.stable.core.common.action

/**
 * The action state is used to contains multiple action output once an action has been completed such as validation.
 *
 * @property actionStatus The action status
 * @property actionStatusMessage The message to describe the action status.
 * @property actionValue Value to hint what value may lead those certain action state.
 * @constructor Create empty Action state
 */
data class ActionState(
    val actionStatus: ActionStatus,
    val actionStatusMessage: String = "",
    val actionValue: Any? = null
) {
    companion object {
        fun success(actionStatusMessage: String = "", actionValue: Any? = null): ActionState {
            return ActionState(ActionStatus.SUCCESS, actionStatusMessage, actionValue)
        }
    }

    fun isSuccess(): Boolean {
        return actionStatus == ActionStatus.SUCCESS
    }
}