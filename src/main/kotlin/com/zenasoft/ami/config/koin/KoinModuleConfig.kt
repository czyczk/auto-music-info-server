package com.zenasoft.ami.config.koin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlNamingStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.zenasoft.ami.common.AmiContext
import com.zenasoft.ami.config.appconfig.AppConfigLoader
import com.zenasoft.ami.config.appconfig.NetworkConfig
import com.zenasoft.ami.config.appconfig.NetworkConfigUtil
import com.zenasoft.ami.controller.ExpController
import com.zenasoft.ami.controller.GoogleSearchController
import com.zenasoft.ami.integration.googlesearch.IGoogleSearchClient
import com.zenasoft.ami.integration.googlesearch.impl.GoogleSearchClientImpl
import com.zenasoft.ami.service.googlesearch.IGoogleSearchService
import com.zenasoft.ami.service.googlesearch.impl.GoogleSearchServiceImpl
import com.zenasoft.ami.service.pagefetcher.IPageFetcherService
import com.zenasoft.ami.service.pagefetcher.impl.PageFetcherServiceImpl
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }

class KoinModuleConfig {
    companion object {
        fun prepareInstanceModule(): org.koin.core.module.Module {

            // ObjectMapper
            val jsonObjectMapper = ObjectMapper()
                .registerModule(KotlinModule.Builder().build())
            val yamlObjectMapper = ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
                .registerModule(KotlinModule.Builder().build())
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)

            // KSerializer objects. (The object mappers above can't be integrated with @Serializer in KSerializer.)
            // JSON KSerializer
            val jsonKSerializer = Json { ignoreUnknownKeys = true }
            // Create a Yaml serializer with kebab-case naming strategy.
            val yamlKSerializerConfiguration = YamlConfiguration(yamlNamingStrategy = YamlNamingStrategy.KebabCase)
            val yamlKSerializer = Yaml(configuration = yamlKSerializerConfiguration)

            // Load the app config
            val appConfig = AppConfigLoader.load(yamlKSerializer)
            logger.info("appConfig: ${jsonObjectMapper.writeValueAsString(appConfig)}")

            // OkHttpClient
            val okHttpClient = createOkHttpClient(appConfig.network)

            // Construct a module
            val instanceModule = module {
                // Basic components
                single(named("jsonObjectMapper")) { jsonObjectMapper }
                single(named("yamlObjectMapper")) { yamlObjectMapper }
                single { jsonKSerializer }
                single { yamlKSerializer }
                single { appConfig }
                single {
                    // AmiContext
                    AmiContext(get())
                }
                single { okHttpClient }

                // Integration
                single { GoogleSearchClientImpl() } bind IGoogleSearchClient::class

                // Service
                single { PageFetcherServiceImpl() } bind IPageFetcherService::class
                single { GoogleSearchServiceImpl() } bind IGoogleSearchService::class

                // Controller
                single { ExpController() }
                single { GoogleSearchController() }
//                single { virtualRootEntry }
//                single(named<VirtualRootEntry>()) { virtualRootEntry } bind IEntry::class

            }

            return instanceModule
        }

        private fun createOkHttpClient(networkConfig: NetworkConfig): OkHttpClient {
            val proxy = networkConfig.proxyUrl?.let {
                NetworkConfigUtil.createProxyFromUrl(it)
            }
            val okHttpClientBuilder = OkHttpClient.Builder()
            val okHttpConnectionPool = ConnectionPool(20, 5, TimeUnit.MINUTES)
            okHttpClientBuilder.connectionPool(okHttpConnectionPool)
            proxy?.let {
                okHttpClientBuilder.proxy(it)
            }
            return okHttpClientBuilder.build()
        }
    }
}