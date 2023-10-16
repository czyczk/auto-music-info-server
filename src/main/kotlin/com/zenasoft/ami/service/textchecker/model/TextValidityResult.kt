package com.zenasoft.ami.service.textchecker.model

import kotlinx.serialization.Serializable

@Serializable
class TextValidityResult(
    val isValid: Boolean,
    val invalidIndex: Int?,
    val invalidChar: Char?,
    val invalidReason: String?,
) {

    companion object {

        fun ofValid(): TextValidityResult {
            return TextValidityResult(true, null, null, null)
        }

        fun ofInvalid(invalidIndex: Int, invalidChar: Char, invalidReason: String): TextValidityResult {
            return TextValidityResult(false, invalidIndex, invalidChar, invalidReason)
        }

    }

}