package entities

import java.math.BigInteger

data class BlindedModInv (
    val number: BigInteger,
    val module: BigInteger,
    val random: BigInteger,
    val blindedNumber: BigInteger
)