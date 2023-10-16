package com.zenasoft.ami.service.textchecker.impl

import com.zenasoft.ami.common.type.TextLanguageEnum
import com.zenasoft.ami.config.appconfig.AppConfig
import com.zenasoft.ami.service.textchecker.ITextCheckerService
import com.zenasoft.ami.service.textchecker.model.TextValidityResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val commonConfigKeyStr = "common"

class TextCheckerServiceImpl : ITextCheckerService, KoinComponent {

    private val appConfig: AppConfig by inject()

    override fun checkTextValidity(text: String, textLanguage: TextLanguageEnum?): TextValidityResult {
        // If `textLanguage` is not specified, check the validity against "common" rules.
        // Otherwise, check the validity against the rules of the specified text language additional to "common" rules.

        val commonCharacterRules = appConfig.textChecker.characterBlacklist[commonConfigKeyStr]
        if (!commonCharacterRules.isNullOrEmpty()) {
            text.forEachIndexed() { index, char ->
                if (commonCharacterRules.contains(char)) {
                    return TextValidityResult.ofInvalid(index, char, "Violates single characters of common rules")
                }
            }
        }

        val commonRangeRules = appConfig.textChecker.rangeBlacklist[commonConfigKeyStr]
        if (!commonRangeRules.isNullOrEmpty()) {
            text.forEachIndexed() { index, char ->
                val codePoint = char.code
                commonRangeRules.forEach() { rangeBlacklistEntry ->
                    if (codePoint in rangeBlacklistEntry.start..rangeBlacklistEntry.end) {
                        return TextValidityResult.ofInvalid(
                            index,
                            char,
                            "Violates range blacklist of common rules"
                        )
                    }
                }
            }
        }

        if (textLanguage != null) {
            val textLanguageRules = appConfig.textChecker.characterBlacklist[textLanguage.configKeyStr]
            if (!textLanguageRules.isNullOrEmpty()) {
                text.forEachIndexed() { index, char ->
                    if (textLanguageRules.contains(char)) {
                        return TextValidityResult.ofInvalid(
                            index, char, "Violates single characters of ${textLanguage.name} rules"
                        )
                    }
                }
            }

            val textLanguageRangeRules = appConfig.textChecker.rangeBlacklist[textLanguage.configKeyStr]
            if (!textLanguageRangeRules.isNullOrEmpty()) {
                text.forEachIndexed() { index, char ->
                    val codePoint = char.code
                    textLanguageRangeRules.forEach() { rangeBlacklistEntry ->
                        if (codePoint in rangeBlacklistEntry.start..rangeBlacklistEntry.end) {
                            return TextValidityResult.ofInvalid(
                                index,
                                char,
                                "Violates range blacklist of ${textLanguage.name} rules"
                            )
                        }
                    }
                }
            }
        }

        return TextValidityResult.ofValid()
    }

}