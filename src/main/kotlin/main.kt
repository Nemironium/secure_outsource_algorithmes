import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.check
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import di.getApplicationModules
import entities.ModExp
import entities.ModInv
import entities.Mode
import managers.CalculationManager
import io.ktor.util.*
import kotlinx.coroutines.*
import managers.FileManager
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import entities.OperationResult
import java.nio.file.Path

@KtorExperimentalAPI
class Cli: CliktCommand(
    help = "Calculate modular exponentiation, or modular inverse from FILE",
    printHelpOnEmptyArgs = true
), KoinComponent {

    private val remote: Boolean by option(
        help = "Calculate values locally or on remote server")
        .flag(default = false)

    private val checked: Boolean by option(
            help = "Algorithm with check or not")
            .flag(default = false)

    private val mode: Mode by argument(
        help = "What to calculate: modular exponentiation(modexp) / modular inverse(modinv")
        .enum(ignoreCase = true)

    private val path: Path by argument(
        name = "FILE",
        help = "File with initial parameters in JSON format")
        .path(mustBeReadable = true, mustExist = true)
        .check(message = "File must be in .json format") { path ->
            path.extension == "json" }

    private val calculationManager by inject<CalculationManager>()
    private val fileManager by inject<FileManager>()

    override fun run() {
        // initializing DI
        echo("Initializing DI modules...")
        initKoin(remote)

        echo("mode = $mode")

        when(mode) {
            Mode.MODEXP -> {
                runBlocking {
                    launch {
                        echo("checked = $checked")
                        echo("Reading parameters from file...")
                        when(val data = fileManager.processReadFile(path.toString(), ModExp.serializer())) {
                            is OperationResult.Success -> {
                                echo("data from file = ${data.data}")
                                val result = calculationManager.processModExp(data.data, isChecked = checked)
                                echo("result = $result")
                            }
                            is OperationResult.Error -> { throw UsageError(data.error) }
                        }
                    }
                }
            }
            Mode.MODINV -> {
                runBlocking {
                    launch {
                        echo("Reading parameters from file...")
                        when(val data = fileManager.processReadFile(path.toString(), ModInv.serializer())) {
                            is OperationResult.Success -> {
                                echo("data from file = ${data.data}")
                                val result = calculationManager.processModInv(data.data)
                                echo("result = $result")
                            }
                            is OperationResult.Error -> { throw UsageError(data.error) }
                        }
                    }
                }
            }
        }
    }



    private fun initKoin(isRemote: Boolean) {
        startKoin {
            modules(getApplicationModules(isRemote))
        }
    }
}

@KtorExperimentalAPI
fun main(args: Array<String>) = Cli().main(args)
