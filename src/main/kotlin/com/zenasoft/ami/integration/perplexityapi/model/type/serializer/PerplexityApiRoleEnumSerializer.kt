package com.zenasoft.ami.integration.perplexityapi.model.type.serializer

import com.zenasoft.ami.integration.perplexityapi.model.type.RoleEnum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PerplexityApiRoleEnumSerializer : KSerializer<RoleEnum> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("RoleEnum", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RoleEnum) =
        encoder.encodeString(value.apiCode)

    override fun deserialize(decoder: Decoder): RoleEnum {
        val apiCode = decoder.decodeString()
        return RoleEnum.fromApiCode(apiCode)
    }

}