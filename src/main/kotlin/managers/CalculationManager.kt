package managers

import com.github.ajalt.clikt.output.TermUi.echo
import entities.*
import kotlinx.coroutines.*
import service.CalculationService

import entities.OperationResult
import util.multOf
import java.math.BigInteger

class CalculationManager(
    private val service: CalculationService
) {

    suspend fun processModInv(data: ModInv): OperationResult<BigInteger> {
        return withContext(Dispatchers.Default) {
            val blindedData = data.blind()
            val blindedResult = service.calculateModInv(data.copy(
                number = blindedData.blindedNumber,
                module = blindedData.module)
            )

            echo("blindedData = $blindedData")
            echo("blindedResult = ${blindedResult.result}")
            checkModInvResult(blindedData, blindedResult)
        }
    }

    suspend fun processModExp(data: ModExp, isChecked: Boolean): OperationResult<BigInteger> {
        return withContext(Dispatchers.Default) {
            val blindParameters = data.generateBlindParameters()
            val queryData = if(isChecked) {
                val queryParameters = data.generateBlindParameters()
                echo("queryParameters = $queryParameters")
                ModExp(queryParameters.blindedBase1, queryParameters.blindedExponent1, data.module)
            } else null

            echo("blindParameters = $blindParameters")
            
            val requestsList = listOf(
                executeRequest(this, ModExp(blindParameters.blindedBase1, blindParameters.blindedExponent1, blindParameters.module), queryData),
                executeRequest(this, ModExp(blindParameters.blindedBase1, blindParameters.blindedExponent2, blindParameters.module), queryData),
                executeRequest(this, ModExp(blindParameters.blindedBase2, blindParameters.blindedExponent1, blindParameters.module), queryData),
                executeRequest(this, ModExp(blindParameters.blindedBase2, blindParameters.blindedExponent2, blindParameters.module), queryData)
            ).flatten()

            val resultList = processResult(requestsList)

            if (requestsList.size != resultList.size || requestsList.size % 2 == 1) {
                val err = "Calculations failed. processed data = $data, resultList = $resultList"
                return@withContext OperationResult.Error(err)
            }
            checkModExpResult(resultList, data, isChecked)
        }
    }

    /**
     * Send 2 ModExp request to the server: first for checking, second for calculation in random order
     */
    private suspend fun executeRequest(
        scope: CoroutineScope,
        query: ModExp,
        testQuery: ModExp? = null
    ): List<Pair<Deferred<CalcResult>, String>> {
        return if (testQuery != null) {
            val requests = listOf(Pair(query, "normal"), Pair(testQuery, "query")).shuffled()
            listOf(
                Pair(scope.async { service.calculateModExp(requests.first().first) }, requests.first().second),
                Pair(scope.async { service.calculateModExp(requests.last().first) }, requests.last().second)
            )
        } else {
            listOf(Pair(scope.async { service.calculateModExp(query) }, "normal"))
        }

    }

    /**
     * Unwrap results from server
     */
    private suspend fun processResult(
        requests: List<Pair<Deferred<CalcResult>, String>>
    ): List<BigInteger> {
        val results = mutableListOf<Pair<BigInteger, String>>()
        requests.forEach {
            try {
                results.add(Pair(it.first.await().result, it.second))
            } catch (e: Exception) { }
        }
        results.sortBy { it.second }
        return results.map { it.first }
    }

    /**
     * Check correctness from server
     */
    private fun checkModExpResult(
        results: List<BigInteger>,
        data: ModExp,
        isChecked: Boolean
    ): OperationResult<BigInteger> {
        if (isChecked) {
            val queryResults = results.subList(results.size / 2, results.size)
            val normalResults = results.subList(0, results.size / 2)

            echo("queryResults = $queryResults, normalResults = $normalResults")

            val check = queryResults.all { it == queryResults.first() }
            val result = normalResults.multOf().mod(data.module)

            return if (check && result != BigInteger.ONE) {
                OperationResult.Success(result)
            } else {
                OperationResult.Error("Check failed. data = $data, results = $results")
            }
        } else {
            val result = results.multOf().mod(data.module)
            echo("results = $results")
            return if (result != BigInteger.ONE)
                OperationResult.Success(result)
            else
                OperationResult.Error("Calculations failed.  data = $data, results = $results")
        }
    }

    private fun checkModInvResult(
            blindedData: BlindedModInv,
            blindedResult: CalcResult
    ): OperationResult<BigInteger> {
        val check = (blindedResult.result * blindedData.blindedNumber).mod(blindedData.module)

        return if (check == BigInteger.ONE) {
            val result = (blindedResult.result * blindedData.random).mod(blindedData.module)
            OperationResult.Success(result)
        } else {
            val err = "Wrong response from server. blindedData = $blindedData, blindedResult = ${blindedResult.result}"
            OperationResult.Error(err)
        }
    }
}