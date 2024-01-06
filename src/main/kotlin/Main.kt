import com.zenasoft.ami.config.koin.InstanceModuleConfig
import com.zenasoft.ami.runner.Runner
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main(args: Array<String>) {
    try {
        val envProfile = System.getenv("ENV_PROFILE") ?: "prod"
        val instanceModule = InstanceModuleConfig.prepareInstanceModule(envProfile)

        startKoin {
            modules(instanceModule)
        }

        Runner.run(args)
    } finally {
        stopKoin()
    }
}