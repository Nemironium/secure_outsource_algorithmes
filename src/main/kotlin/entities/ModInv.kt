@file:UseSerializers(BigIntegerSerializer::class)
package entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import util.BigIntegerSerializer
import util.nextRandom
import java.math.BigInteger

@Serializable
data class ModInv(
    val number: BigInteger,
    val module: BigInteger
)

fun ModInv.blind(): BlindedModInv {
    val tempRandom = module.nextRandom
    return BlindedModInv(
        number = number,
        module = module,
        random = tempRandom,
        blindedNumber = (tempRandom * number).mod(module)
    )
}