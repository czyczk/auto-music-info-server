package com.zenasoft.ami.config.appconfig

import com.charleskorn.kaml.Yaml
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.internal.closeQuietly
import java.io.File
import java.io.FileInputStream

class AppConfigLoader {

    companion object {
        private val configPath = "/config.yml"
        private val utConfigPath = "/config-ut.yml"
        private val alternativeConfigPath = System.getProperty("user.home") + "/.config/ami-server/config.yml"

        fun load(yamlObjectMapper: ObjectMapper): AppConfig {
            var configIs = AppConfigLoader::class.java.getResourceAsStream(configPath)
            if (configIs == null) {
                // Check if the alternative config path exists.
                val alternativeConfigFile = File(alternativeConfigPath)
                if (!alternativeConfigFile.exists()) {
                    throw RuntimeException("`configIs` is null. Check if file \"config.yml\" is present in \"\$HOME/.config/ami-server\".")
                }
                configIs = FileInputStream(alternativeConfigPath)
            }

            try {
                return yamlObjectMapper.readValue(
                    configIs,
                    AppConfig::class.java
                )
            } finally {
                configIs.closeQuietly()
            }
        }

        fun load(envProfile: String, yamlKSerializer: Yaml): AppConfig {
            val configPath = when (envProfile) {
                "prod" -> configPath
                "ut" -> utConfigPath
                else -> throw IllegalArgumentException("Invalid envProfile: $envProfile")
            }
            var configIs = AppConfigLoader::class.java.getResourceAsStream(configPath)
            if (configIs == null) {
                // Check if the alternative config path exists.
                val alternativeConfigFile = File(alternativeConfigPath)
                if (!alternativeConfigFile.exists()) {
                    throw RuntimeException("`configIs` is null. Check if file \"config.yml\" is present in \"\$HOME/.config/ami-server\".")
                }
                configIs = FileInputStream(alternativeConfigPath)
            }

            try {
                return yamlKSerializer.decodeFromString(
                    AppConfig.serializer(),
                    configIs.readAllBytes().toString(Charsets.UTF_8)
                )
            } finally {
                configIs.closeQuietly()
            }
        }
    }

}