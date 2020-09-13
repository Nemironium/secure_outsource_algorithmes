package service

import entities.ModExp
import entities.ModInv
import entities.CalcResult

interface CalculationService {
    /* TODO(try to return BigInteger from functions) */
    suspend fun calculateModExp(data: ModExp): CalcResult
    suspend fun calculateModInv(data: ModInv): CalcResult
}