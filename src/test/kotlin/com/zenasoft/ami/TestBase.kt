package com.zenasoft.ami

import com.zenasoft.ami.config.koin.InstanceModuleConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest

abstract class TestBase : KoinTest {

    protected val envProfile = System.getenv("ENV_PROFILE") ?: "ut"

    init {
        // Hard-coded JVM properties for ut.
        System.setProperty("app.encryption-secret-key", "123456")
    }

    @BeforeEach
    fun setup() {
        val instanceModule = InstanceModuleConfig.prepareInstanceModule(envProfile)

        startKoin {
            modules(instanceModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

}