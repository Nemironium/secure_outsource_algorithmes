package di

import managers.CalculationManager
import managers.FileManager
import io.ktor.util.*
import org.koin.dsl.module
import service.CalculationService
import service.CalculationServiceApi
import service.CalculationServiceProxy
import util.HttpClientFactory
import util.JsonFactory
import util.UrlProvider

@KtorExperimentalAPI
fun getApplicationModules(isRemote: Boolean) = module {

    // JSON config initializing
    single { JsonFactory().create() }

    single { FileManager(json = get()) }

    when(isRemote) {
        true -> {
            // HTTP client initializing
            single { HttpClientFactory(json = get()).create() }

            // TODO("provide proper URLs!")
            val modInvUrls = listOf("test.com/modinv")
            val modExpUrls = listOf("test.com/modexp")

            // URL provider initializing
            single { UrlProvider(modInvUrls = modInvUrls,modExpUrls = modExpUrls ) }

            single { CalculationServiceApi(client = get(), urlProvider = get()) as CalculationService }
        }
        false -> {
            single { CalculationServiceProxy() as CalculationService }
        }
    }

    single { CalculationManager(service = get()) }
}