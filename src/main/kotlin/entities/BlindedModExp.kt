package entities

import java.math.BigInteger

data class BlindedModExp (
    val module: BigInteger,
    val blindedBase1: BigInteger,
    val blindedExponent1: BigInteger,
    val blindedBase2: BigInteger,
    val blindedExponent2: BigInteger
)
