import com.zenasoft.ami.config.koin.KoinModuleConfig
import com.zenasoft.ami.runner.Runner
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun main(args: Array<String>) {
    try {
        val instanceModule = KoinModuleConfig.prepareInstanceModule()

        startKoin {
            modules(instanceModule)
        }

        Runner.run(args)
    } finally {
        stopKoin()
    }
}