package org.iheartradio.techtalk.utils.extensions

import kotlinx.nosql.*
import kotlinx.nosql.query.GreaterEqualQuery
import kotlinx.nosql.query.GreaterQuery

/**
 * Look at class kotlinx.nosql.AbstractColumn for list of all others.
 *
 * For some reason, this library DOESN'T support Long types on WHERE clause operations.
 *
 * We might want to think about changing the ID of an Entity to an Int instead of Long
 * since this Kotlin-NoSQL library does not support Long types operations of
 * WHERE clause operations
 */


//region New Functions for 'Long' type

infix fun <T : AbstractSchema> AbstractColumn<out Long?, T, Long>.isGreaterThan(other: Long): Query {
    return GreaterQuery(this, LiteralExpression(other.toInt()))
}

//infix fun <T : AbstractSchema> AbstractColumn<out Long?, T, Long>.isGreaterThan(other: Expression<out Long?>): Query {
//    return GreaterQuery(this, other)
//}


//endregion

/**
 * kotlinx.nosql.AbstractColumn related function is called 'gt'. But I wanted to rename all
 * of this functions and make them more readable as infix functions
 */

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isGreaterThan(other: Int): Query {
    return GreaterQuery(this, LiteralExpression(other))
}

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isGreaterThan(other: Expression<out Int?>): Query {
    return GreaterQuery(this, other)
}

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isGreaterThanOrEqualTo(other: Int): Query = ge(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isGreaterThanOrEqualTo(other: Expression<out Int?>): Query = ge(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.le(other: Expression<out Int?>): Query = le(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.le(other: Int): Query  = le(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.lt(other: Expression<out Int?>): Query = lt(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.lt(other: Int): Query = lt(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isEqualTo(other: Int): Query = equal(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isEqualTo(other: Expression<out Int?>): Query = equal(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isNotEqualTo(other: Int): Query = notEqual(other)

infix fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.isNotEqualTo(other: Expression<out Int?>): Query = notEqual(other)

/*


fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.le(other: Expression<out Int?>): Query {
    return LessEqualQuery(this, other)
}

fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.le(other: Int): Query {
    return LessEqualQuery(this, LiteralExpression(other))
}

fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.lt(other: Expression<out Int?>): Query {
    return LessQuery(this, other)
}

fun <T : AbstractSchema> AbstractColumn<out Int?, T, Int>.lt(other: Int): Query {
    return LessQuery(this, LiteralExpression(other))
}
 */