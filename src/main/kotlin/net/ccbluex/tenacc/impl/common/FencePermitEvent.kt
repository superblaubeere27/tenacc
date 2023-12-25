package net.ccbluex.tenacc.impl.common

import net.ccbluex.tenacc.api.common.CIEvent

class FencePermitEvent(val permissions: List<Int>): CIEvent() {
}