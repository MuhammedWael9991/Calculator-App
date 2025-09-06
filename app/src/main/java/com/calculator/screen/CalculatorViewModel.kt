package com.calculator.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notkamui.keval.Keval

class CalculatorViewModel: ViewModel() , InteractionListener {

    private val _uiState = MutableLiveData(UiState())
    val uiState: LiveData<UiState> = _uiState
    val operators = listOf("+", "-", "x", "÷" , "%")

    override fun onNumberClick(text: String) {
        val oldResult = _uiState.value?.result ?: ""
        if (_uiState.value?.isDone == true && text !in operators && oldResult == "0" || _uiState.value?.isError == true){
            _uiState.value = _uiState.value?.copy(
                result = text,
                isDone = false,
                isError = false
            )
        }else{
            _uiState.value = _uiState.value?.copy(
                result = oldResult + text
            )
        }

    }

    override fun onEqualClick() {
        val oldResult = _uiState.value?.result ?: ""
        val normalized = evaluateExpression(oldResult)
        val result =
            try {
                formatResult(Keval.eval(normalized))
            }catch (e: Exception){
                _uiState.value = _uiState.value?.copy(
                    isError = true
                )
                "Error\n${e.message}"
            }
        _uiState.value = _uiState.value?.copy(
            expression = oldResult,
            result = result,
            isDone = true
        )
    }

    override fun onClearClick() {
        _uiState.value = _uiState.value?.copy(
            result = "",
            expression = ""
        )
    }

    override fun onDeleteLastDigit() {
        val oldResult = _uiState.value?.result ?: ""
        if (oldResult.isNotEmpty()) {
            if (oldResult.length > 1 && oldResult[oldResult.lastIndex-1].toString() == " "){
                _uiState.value = _uiState.value?.copy(
                    result = oldResult.dropLast(2)
                )
            }else{
                _uiState.value = _uiState.value?.copy(
                    result = oldResult.dropLast(1)
                )
            }
        }
    }

    override fun onOperatorClick(text: String) {
        val oldResult = _uiState.value?.result ?: ""
        val space = " "
        if (oldResult.isNotEmpty() && oldResult != "0"){
            val lastChar = if (oldResult.last().toString() == " "){oldResult[oldResult.lastIndex-1].toString()} else oldResult.last()
            if (lastChar in operators){
                if (lastChar != text){
                    _uiState.value = _uiState.value?.copy(
                        result = "${oldResult.dropLast(3)+ space + text} "
                    )
                }
            }else {
                _uiState.value = _uiState.value?.copy(
                    result = "${oldResult + space + text} "
                )
            }
        }
    }

    override fun onClickPlusMinus() {
        val oldResult = _uiState.value?.result ?: ""
        if (oldResult.isNotEmpty()){
            for (i in oldResult.length-1 downTo 0){
                if (oldResult[i].toString() == "-"){
                    if (i > 0 && oldResult[i-1].toString() == " "){
                        val updatedResult = oldResult.substring(0 , i - 1) + " +" + oldResult.substring(i + 1)
                        _uiState.value = _uiState.value?.copy(
                            result = updatedResult
                        )
                        break
                    }
                }
                if (oldResult[i].toString() == "-"){
                    val updatedResult = oldResult.removeRange(i, i + 1)
                    _uiState.value = _uiState.value?.copy(
                        result = updatedResult
                    )
                    break
                }else if (oldResult[i].toString() in listOf("x", "÷" , "%" , "+")){
                    val updatedResult = oldResult.substring(0, i + 1) + "-" + oldResult.substring(i + 1)
                    _uiState.value = _uiState.value?.copy(
                        result = updatedResult
                    )
                    break
                } else if (i == 0){
                    val updatedResult = "-$oldResult"
                    _uiState.value = _uiState.value?.copy(
                        result = updatedResult
                    )
                    break
                }
            }

        }
    }

    fun evaluateExpression(expression: String): String{
        return expression
            .replace("x", "*")
            .replace("÷", "/")
    }

    fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            value.toString()
        }
    }

}