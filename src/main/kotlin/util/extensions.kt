package util

import java.math.BigInteger
import java.security.SecureRandom

val BigInteger.nextRandom: BigInteger
    get() = BigInteger(this.bitLength(), SecureRandom()).mod(this)

fun BigInteger.isRelativelyPrime(other: BigInteger): Boolean =
    this.gcd(other) == BigInteger.ONE


fun Iterable<BigInteger>.multOf(): BigInteger {
    var result: BigInteger = BigInteger.ONE
    for (element in this) {
        result *= element
    }
    return result
}
