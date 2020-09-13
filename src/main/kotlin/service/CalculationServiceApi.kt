package service

import entities.ModExp
import entities.ModInv
import entities.CalcResult
import io.ktor.client.*
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import util.UrlProvider

class CalculationServiceApi(
    private val client: HttpClient,
    private val urlProvider: UrlProvider
) : CalculationService {
    override suspend fun calculateModExp(data: ModExp): CalcResult {
        return client.post(urlProvider.provideModExpUrl()) {
            body = data
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun calculateModInv(data: ModInv): CalcResult {
        return client.post(urlProvider.provideModInvUrl()) {
            body = data
            contentType(ContentType.Application.Json)
        }
    }
}