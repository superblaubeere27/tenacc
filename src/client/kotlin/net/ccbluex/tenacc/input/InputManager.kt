package net.ccbluex.tenacc.input

import net.ccbluex.tenacc.api.client.InputKey
import net.ccbluex.tenacc.client.interfaces.IMixinKeyBinding
import java.util.*

object InputManager {
    val currentInputs = Collections.synchronizedList(ArrayList<ForcedInput>())

    fun setInput(inputKeys: Array<out InputKey>, nTicks: Int?) {
        for (inputKey in inputKeys) {
            currentInputs.add(ForcedInput(nTicks, inputKey))

            val keyBinding = INPUT_KEY_TO_KEYBINDING[inputKey]!!

            (keyBinding as IMixinKeyBinding).press(1)
        }
    }

    fun clearInput() {
        this.currentInputs.clear()
    }

    fun tickInput() {
        for (currentInput in this.currentInputs) {
            val ticksLeft = currentInput.nTicksLeft ?: continue

            currentInput.nTicksLeft = ticksLeft - 1
        }

        this.currentInputs.removeIf { (it.nTicksLeft ?: 1) <= 0 }
    }

    fun isInputPressed(inputKey: InputKey) = this.currentInputs.any { it.key == inputKey }
}

class ForcedInput(var nTicksLeft: Int?, val key: InputKey)