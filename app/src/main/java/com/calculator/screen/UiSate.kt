package com.calculator.screen

data class UiState(
    val expression: String = "",
    val result: String = "",
    val isDone: Boolean = false,
    val isError: Boolean = false
)