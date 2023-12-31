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
import com.zenasoft.ami.controller.InfoExtractorController
import com.zenasoft.ami.controller.SearchController
import com.zenasoft.ami.controller.TextCheckerController
import com.zenasoft.ami.integration.googlesearch.IGoogleSearchClient
import com.zenasoft.ami.integration.googlesearch.impl.GoogleSearchClientImpl
import com.zenasoft.ami.integration.perplexityapi.IPerplexityApiClient
import com.zenasoft.ami.integration.perplexityapi.impl.PerplexityApiClientImpl
import com.zenasoft.ami.service.googlesearch.IGoogleSearchService
import com.zenasoft.ami.service.googlesearch.impl.GoogleSearchServiceImpl
import com.zenasoft.ami.service.infoextractor.IInfoExtractor
import com.zenasoft.ami.service.infoextractor.impl.GeneralAiPoweredInfoExtractorImpl
import com.zenasoft.ami.service.infoextractor.impl.InfoExtractorSelector
import com.zenasoft.ami.service.pagefetcher.IPageFetcherService
import com.zenasoft.ami.service.pagefetcher.impl.PageFetcherServiceImpl
import com.zenasoft.ami.service.textchecker.ITextCheckerService
import com.zenasoft.ami.service.textchecker.impl.TextCheckerServiceImpl
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import mu.KotlinLogging
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }

object InstanceModuleConfig {
    @OptIn(ExperimentalSerializationApi::class)
    fun prepareInstanceModule(envProfile: String): org.koin.core.module.Module {
        return module {
            // ObjectMapper
            val jsonObjectMapper = ObjectMapper()
                .registerModule(KotlinModule.Builder().build())
            val yamlObjectMapper = ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
                .registerModule(KotlinModule.Builder().build())
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)

            // KSerializer objects. (The object mappers above can't be integrated with @Serializer in KSerializer.)
            // General-purposed JSON KSerializer
            val jsonKSerializer = Json {
                ignoreUnknownKeys = true
            }
            // General-purposed Yaml serializer with kebab-case naming strategy.
            val yamlKSerializerConfiguration = YamlConfiguration(yamlNamingStrategy = YamlNamingStrategy.KebabCase)
            val yamlKSerializer = Yaml(configuration = yamlKSerializerConfiguration)

            // Perplexity API client: JSON KSerializer
            // - Snake-case naming strategy
            val perplexityApiJsonKSerializer = Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
                ignoreUnknownKeys = true
            }

            // Load the app config
            val appConfig = AppConfigLoader.load(envProfile, yamlKSerializer)
            logger.info("appConfig: ${jsonObjectMapper.writeValueAsString(appConfig)}")

            // OkHttpClient
            val okHttpClient = createOkHttpClient(appConfig.network)
            // KtorClient
            val ktorClient = createKtorClient(appConfig.network, null)
            val perplexityApiKtorClient =
                createKtorClient(appConfig.network, appConfig.externalService.perplexityAi.apiKey)

            // Register components
            // Basic components
            single(named("jsonObjectMapper")) { jsonObjectMapper }
            single(named("yamlObjectMapper")) { yamlObjectMapper }
            single { jsonKSerializer }
            single { yamlKSerializer }
            single(named("perplexityApiJsonKSerializer")) { perplexityApiJsonKSerializer }

            single { appConfig }
            single {
                // AmiContext
                AmiContext(get())
            }
            single { okHttpClient }
            single { ktorClient }
            single(named("perplexityApiKtorClient")) { perplexityApiKtorClient }

            // Integration
            single { GoogleSearchClientImpl() } bind IGoogleSearchClient::class
            single { PerplexityApiClientImpl() } bind IPerplexityApiClient::class

            // Service
            single { GoogleSearchServiceImpl() } bind IGoogleSearchService::class
            single { PageFetcherServiceImpl() } bind IPageFetcherService::class
            single(named("generalAiPoweredInfoExtractor")) { GeneralAiPoweredInfoExtractorImpl() } bind IInfoExtractor::class
            single(named("infoExtractorSelector")) { InfoExtractorSelector() } bind IInfoExtractor::class
            single { TextCheckerServiceImpl() } bind ITextCheckerService::class

            // Controller
            single { ExpController() }
            single { SearchController() }
            single { InfoExtractorController() }
            single { TextCheckerController() }
        }
    }

    private fun createOkHttpClient(networkConfig: NetworkConfig): OkHttpClient {
        val proxy = networkConfig.proxyHost?.let {
            val url = "${networkConfig.proxyProtocol!!}://$it:${networkConfig.proxyPort!!}"
            NetworkConfigUtil.createProxyFromUrl(url)
        }
        val okHttpClientBuilder = OkHttpClient.Builder()
        val okHttpConnectionPool = ConnectionPool(20, 5, TimeUnit.MINUTES)
        okHttpClientBuilder.connectionPool(okHttpConnectionPool)
        proxy?.let {
            okHttpClientBuilder.proxy(it)
        }
        return okHttpClientBuilder.build()
    }

    private fun createKtorClient(networkConfig: NetworkConfig, bearerAccessToken: String?): HttpClient {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 10_000
            }
            bearerAccessToken?.let {
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(it, "")
                        }
                    }
                }
            }
            engine {
                proxy = networkConfig.proxyHost?.let {
                    when (networkConfig.proxyProtocol) {
                        "http" -> {
                            val url = "http://$it:${networkConfig.proxyPort!!}"
                            ProxyBuilder.http(url)
                        }

                        "socks5" -> {
                            ProxyBuilder.socks(it, networkConfig.proxyPort!!)
                        }

                        else -> {
                            throw IllegalArgumentException("Unsupported proxy protocol: ${networkConfig.proxyProtocol}")
                        }
                    }
                }
            }
        }
        return client
    }
}