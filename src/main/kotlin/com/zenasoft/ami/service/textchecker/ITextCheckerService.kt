package com.zenasoft.ami.service.textchecker

import com.zenasoft.ami.common.type.TextLanguageEnum
import com.zenasoft.ami.service.textchecker.model.TextValidityResult

interface ITextCheckerService {

    fun checkTextValidity(text: String, textLanguage: TextLanguageEnum?): TextValidityResult

}