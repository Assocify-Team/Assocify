package com.github.se.assocify.ui.util

object PriceUtil {

    const val ZERO_PRICE_STRING = "0.0"
    fun toString(price: Double): String {
        /*TODO: Implement currency formatting and add appropriate checks*/
        return price.toString()
    }

    fun toDouble(price: String): Double {
        /*TODO: Remove - and add appropriate checks*/
        return price.toDouble()
    }
}