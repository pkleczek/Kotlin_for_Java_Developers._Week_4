package rationals

import java.math.BigInteger

class Rational private constructor(val nominator: BigInteger, val denominator: BigInteger) : Comparable<Rational> {
    companion object {
        fun create(nominator: BigInteger, denominator: BigInteger): Rational {
            val gcd = nominator.gcd(denominator)
            val signum = nominator.signum() * denominator.signum()
            return Rational(
                nominator.abs() * signum / gcd,
                denominator.abs() / gcd
            )
        }
    }

    init {
        if (denominator == BigInteger.ZERO) throw IllegalArgumentException("Denominator can't be zero!")
    }

    operator fun plus(other: Rational): Rational {
        val base = denominator * other.denominator
        val first = nominator * (base / denominator)
        val second = other.nominator * (base / other.denominator)
        return create(first + second, base)
    }

    operator fun minus(other: Rational): Rational = plus(create(other.nominator.negate(), other.denominator))

    operator fun times(other: Rational): Rational = create(nominator * other.nominator, denominator * other.denominator)

    operator fun div(other: Rational): Rational = times(create(other.denominator, other.nominator))

    operator fun unaryMinus(): Rational = create(this.nominator.negate(), this.denominator)

    override fun compareTo(other: Rational): Int = (this - other).nominator.signum()

    override fun toString(): String {
        return if (denominator == BigInteger.ONE) nominator.toString() else "${nominator}/${denominator}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (nominator != other.nominator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nominator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}

infix fun Int.divBy(denominator: Int) = Rational.create(this.toBigInteger(), denominator.toBigInteger())
infix fun Long.divBy(denominator: Long) = Rational.create(this.toBigInteger(), denominator.toBigInteger())
infix fun BigInteger.divBy(denominator: BigInteger) = Rational.create(this, denominator)
fun String.toRational(): Rational {
    val split = this.split("/")

    return when (split.size) {
        1 -> {
            val (n) = split
            Rational.create(n.toBigInteger(), BigInteger.ONE)
        }
        2 -> {
            val (n, d) = split
            Rational.create(n.toBigInteger(), d.toBigInteger())
        }
        else -> throw IllegalArgumentException("Unable to detect Rational")
    }
}

private operator fun BigInteger.times(i: Int): BigInteger = this * i.toBigInteger()


fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println(
        "912016490186296920119201192141970416029".toBigInteger() divBy
                "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2
    )
}

