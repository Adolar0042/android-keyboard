package org.futo.inputmethod.latin.uix.actions

import android.view.KeyEvent
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.KeyboardManagerForAction

/*
private fun getMetaState(manager: KeyboardManagerForAction): Int =
    if(manager.isShifted()) {
        KeyEvent.META_SHIFT_ON
    } else {
        0
    }
*/

// Disabled due to confusion and inconsistent app behavior
private fun getMetaState(manager: KeyboardManagerForAction): Int = 0


val ArrowUpAction = Action(
    icon = R.drawable.chevron_up,
    name = R.string.arrow_up_action_title,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_DPAD_UP, getMetaState(manager))
    },
    windowImpl = null,
)

val ArrowDownAction = Action(
    icon = R.drawable.chevron_down,
    name = R.string.arrow_down_action_title,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, getMetaState(manager))
    },
    windowImpl = null,
)

val ArrowLeftAction = Action(
    icon = R.drawable.chevron_left,
    name = R.string.arrow_left_action_title,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, getMetaState(manager))
    },
    windowImpl = null,
)

val ArrowRightAction = Action(
    icon = R.drawable.chevron_right,
    name = R.string.arrow_right_action_title,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, getMetaState(manager))
    },
    windowImpl = null,
)
