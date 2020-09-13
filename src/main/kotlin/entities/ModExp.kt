@file:UseSerializers(BigIntegerSerializer::class)
package entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import util.BigIntegerSerializer
import util.isRelativelyPrime
import util.nextRandom
import java.math.BigInteger

@Serializable
data class ModExp(
        val base: BigInteger,
        val exponent: BigInteger,
        val module: BigInteger
)

fun ModExp.generateBlindParameters(): BlindedModExp {
        var randomBase1: BigInteger
        do {
                randomBase1 = module.nextRandom
        } while (!randomBase1.isRelativelyPrime(module) || randomBase1 == BigInteger.ZERO)

        val randomBase2 = (base * randomBase1.modInverse(module)).mod(module)

        val randomExponent1 = exponent.nextRandom
        val randomExponent2 = (exponent - randomExponent1).mod(exponent)

        return BlindedModExp(
                module = module,
                blindedBase1 = randomBase1,
                blindedBase2 = randomBase2,
                blindedExponent1 = randomExponent1,
                blindedExponent2 = randomExponent2
        )
}