package com.zenasoft.ami.common.exception

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable(with = AmiExceptionSerializer::class)
class AmiException(
    cause: Throwable?,
    val code: AmiErrorCode,
    @Transient
    vararg val params: String = emptyArray(),
) : RuntimeException(cause) {

    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val message: String
        get() = code.messageTemplate.format(*params)

    companion object {
        fun of(errorCode: AmiErrorCode, vararg params: String): AmiException {
            return AmiException(null, errorCode, *params)
        }

        fun of(cause: Throwable, errorCode: AmiErrorCode, vararg params: String): AmiException {
            return AmiException(cause, errorCode, *params)
        }
    }

}