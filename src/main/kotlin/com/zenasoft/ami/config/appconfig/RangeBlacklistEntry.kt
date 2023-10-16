package com.zenasoft.ami.config.appconfig

import kotlinx.serialization.Serializable

@Serializable
class RangeBlacklistEntry {

    /**
     * The start of the range. Inclusive.
     */
    var start: Int = 0

    /**
     * The end of the range. Exclusive.
     */
    var end: Int = Int.MAX_VALUE

}