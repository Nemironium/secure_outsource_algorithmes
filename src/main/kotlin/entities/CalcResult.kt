@file:UseSerializers(BigIntegerSerializer::class)
package entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import util.BigIntegerSerializer
import java.math.BigInteger

@Serializable
data class CalcResult(val result: BigInteger)