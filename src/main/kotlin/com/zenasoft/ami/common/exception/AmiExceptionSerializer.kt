package com.zenasoft.ami.common.exception

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object AmiExceptionSerializer : KSerializer<AmiException> {
    override val descriptor = buildClassSerialDescriptor("AmiException") {
        element<String>("errorCode")
        element<String>("message")
    }

    override fun serialize(encoder: Encoder, value: AmiException) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.code.toString())
            encodeStringElement(descriptor, 1, value.message)
        }
    }

    override fun deserialize(decoder: Decoder): AmiException {
        return decoder.decodeStructure(descriptor) {
            var errorCode: String? = null
            var message: String? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> errorCode = decodeStringElement(descriptor, 0)
                    1 -> message = decodeStringElement(descriptor, 1)
                    else -> break
                }
            }
            AmiException(AmiErrorCode.valueOf(errorCode!!), message!!)
        }
    }
}