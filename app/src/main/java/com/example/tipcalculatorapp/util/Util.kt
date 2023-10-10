package com.example.tipcalculatorapp.util

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty() && tipPercentage!=0) {
        (totalBill * tipPercentage) / 100
    } else {
        0.0
    }
}