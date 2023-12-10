package com.zenasoft.ami.integration.perplexityapi.model.type.serializer

import com.zenasoft.ami.integration.perplexityapi.model.type.ModelEnum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PerplexityApiModelEnumSerializer : KSerializer<ModelEnum> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ModelEnum", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ModelEnum) =
        encoder.encodeString(value.apiCode)

    override fun deserialize(decoder: Decoder): ModelEnum {
        val apiCode = decoder.decodeString()
        return ModelEnum.fromApiCode(apiCode)
    }

}