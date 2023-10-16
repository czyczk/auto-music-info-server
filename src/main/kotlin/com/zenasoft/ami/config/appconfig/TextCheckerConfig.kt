package com.zenasoft.ami.config.appconfig

import kotlinx.serialization.Serializable;

@Serializable
class TextCheckerConfig {

    /**
     * Character blacklist. Language ID -> list of characters.
     */
    lateinit var characterBlacklist: Map<String, List<Char>?>

    /**
     * Range blacklist. Language ID -> list of range blacklist entry.
     */
    lateinit var rangeBlacklist: Map<String, List<RangeBlacklistEntry>?>

}
