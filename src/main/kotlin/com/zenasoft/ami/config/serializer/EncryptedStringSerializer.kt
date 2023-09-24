package com.zenasoft.ami.config.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor

object EncryptedStringSerializer : KSerializer<String> {

    private val secretKey = System.getenv("ENCRYPTION_SECRET_KEY")

    private val encryptor = StandardPBEStringEncryptor()

    init {
        if (secretKey == null) {
            throw IllegalStateException("Env var `ENCRYPTION_SECRET_KEY` is not set")
        }

        encryptor.setPassword(secretKey)
    }

    override fun serialize(encoder: Encoder, value: String) {
        var extractedValue = value

        // Use Jasypt to encrypt the string.
        // If the original value is enclosed with "DEC()" or "ENC()", we'll extract the value inside the parentheses.
        if (isValueEnclosedByMarkers(value)) {
            extractedValue = value.substring(4, value.length - 1)
        }

        // If the original value is enclosed with "DEC()", we'll encrypt it.
        // Otherwise, we'll leave it unchanged.
        if (!value.startsWith("DEC(") || !value.endsWith(")")) {
            encoder.encodeString(value)
            return
        }

        val encryptedValue = encryptor.encrypt(extractedValue)
        encoder.encodeString(encryptedValue)
    }

    override fun deserialize(decoder: Decoder): String {
        val value = decoder.decodeString()
        var extractedValue = value

        // Use Jasypt to decrypt the string.
        // If the original value is enclosed with "DEC()" or "ENC()", we'll extract the value inside the parentheses.
        if (isValueEnclosedByMarkers(value)) {
            extractedValue = value.substring(4, value.length - 1)
        }

        // If the original value is enclosed with "ENC()", we'll decrypt it.
        // Otherwise, we'll leave it unchanged and return the extracted value.
        if (!decoder.decodeString().startsWith("ENC(") || !decoder.decodeString().endsWith(")")) {
            return extractedValue
        }

        return encryptor.decrypt(extractedValue)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("EncryptedString", PrimitiveKind.STRING)

    private fun isValueEnclosedByMarkers(value: String): Boolean {
        return ((value.startsWith("ENC(") || value.startsWith("DEC("))
                && value.endsWith(")"))
    }

}