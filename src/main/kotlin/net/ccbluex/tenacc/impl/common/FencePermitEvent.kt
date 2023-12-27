package net.ccbluex.tenacc.impl.common

import net.ccbluex.tenacc.api.common.TACCEvent

class FencePermitEvent(val permissions: List<Int>): TACCEvent() {
}