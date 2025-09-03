package com.calculator.screen

interface InteractionListener {
    fun onNumberClick(text: String)
    fun onEqualClick()
    fun onClearClick()
    fun onDeleteLastDigit()
    fun onOperatorClick(text: String)
    fun onClickPlusMinus()
}