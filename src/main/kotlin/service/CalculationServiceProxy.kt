package service

import entities.ModExp
import entities.ModInv
import entities.CalcResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class CalculationServiceProxy : CalculationService {
    override suspend fun calculateModExp(data: ModExp): CalcResult {
        return withContext(Dispatchers.Default) {
            CalcResult(data.base.modPow(data.exponent, data.module))
        }
    }

    override suspend fun calculateModInv(data: ModInv): CalcResult {
        return withContext(Dispatchers.Default) {
            CalcResult(data.number.modInverse(data.module))
        }
    }
}