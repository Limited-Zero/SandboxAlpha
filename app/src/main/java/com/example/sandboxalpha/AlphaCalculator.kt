package com.example.sandboxalpha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*
import kotlin.NoSuchElementException

class AlphaCalculator : AppCompatActivity() {

    lateinit var txtInput: TextView
    var lastNumeric: Boolean = false
    var stateError: Boolean = false
    var lastDot: Boolean = false
    var bracketTracker = 0
    var bracketBalance: Boolean = true
    var bracketFiltered: Deque<String> = LinkedList()
    val TAG = "MyActivity"

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alpha_calculator)
        txtInput = findViewById(R.id.txtInput)
        txtInput.movementMethod = ScrollingMovementMethod()
    }

    fun onDigit(view: View) {
        if (stateError) {
            txtInput.text = (view as Button).text
            stateError = false
        } else {
            txtInput.append((view as Button).text)
        }
        lastNumeric = true
    }

    fun onDecimalPoint(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            txtInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onClear(view: View) {
        this.txtInput.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        bracketTracker = 0
    }

    fun onOperator(view: View) {
        if (lastNumeric && !stateError) {
            txtInput.append((view as Button).text)
            lastDot = false
        }
    }

    fun onBracket(view: View){
        if((view as Button).text == "("){ ++bracketTracker }
        if((view as Button).text == ")" && bracketTracker >= 0){ --bracketTracker }
        txtInput.append((view as Button).text)
    }

    fun onBackspace(view: View) {
        if (txtInput.length() > 0) {
            txtInput.text = txtInput.text.subSequence(0, txtInput.length() - 1)
        }
    }

    /*
   bracketOrganizer:
   1. Process string and figure out bracket pairs -------------------------------- []
   2. Throw processed sections into bedmasLogic and return the value --------------[]
   3. Follow algorithm until there are no brackets remaining ----------------------[]
   -
   -
    */

    fun Bfilter(view: View){
        if (lastNumeric && !stateError) {
            var values = txtInput.text.toString()
            var answer = CalcEngine(values);
            txtInput.text = "";

            txtInput.append(answer.toString());
        }
    }


    fun CalcEngine(input:String): Float {
        if (lastNumeric && !stateError) {
            //var values = input
            var numberVal: Deque<Float> = LinkedList()
            var operatorVal: Deque<String> = LinkedList()
            var values = txtInput.text.toString()
            var numericArr: List<String> = (values.split("*", "/", "+", "-").map { it.trim() })
            var operatorArr: List<String> = values.split(Regex("[0-9]|\\.")).map { it.trim() }.filterNotNull()
            //This removes blank entries in operatorArr, and places it into new List
            for (i in 0 until operatorArr.size) {
                if (operatorArr.get(i) != "") {
                    operatorVal.push(operatorArr.get(i))
                }
            }
            //this.txtInput.text = ""
            numericArr.forEach { numberVal.push(it.toFloat()) }
           //numericArr.forEach { txtInput.append(it.toString()) }
            return logic2(numberVal, operatorVal)
        }
        else{
            return 0.0F
        }
    }

    fun logic2(numList: Deque<Float>, opList: Deque<String>): Float{
        var temp1: Float = 0F
        var temp2: Float = 0F
        var operator = ""
        var newNumList: Deque<Float> = LinkedList()
        var newOpList: Deque<String> = LinkedList()
        var answer: Float = -1F
        var skip: Boolean = true
        /*
        Algorithim Steps:
        1: Separate number values and operators ------------------------------------[DONE]
        2: temp values equal to popped values   ------------------------------------[DONE]
        3: if/else logic reads operator --------------------------------------------[DONE]
        4: if MUL/DIV execute operation and push to newNumList ---------------------[DONE]
        5: if ADD/SUB push number to newNumList, and push operator to new stack-----[DONE]
        6: Perform addition and subtraction to get answer---------------------------[DONE]
         */
        while (opList.isNotEmpty()) {
            try {
                temp1 = numList.pop()
            } catch (E: NoSuchElementException) {
                null
            }

            if (skip == false && opList.isNotEmpty()) {
                try {
                    operator = opList.pop()
                } catch (E: NoSuchElementException) {
                    null
                }
            }

            if (operator == "") {
                if (opList.peek() == "+" || opList.peek() == "-") {
                    newNumList.push(temp1)
                }
                answer = temp1
                skip = false
            }
            if (operator == "*") {
                answer *= temp1
                if (opList.peek() == "+" || opList.peek() == "-" || opList.isEmpty()) {
                    newNumList.push(answer)
                }
            } else if (operator == "/") {
                answer = temp1 / answer
                if (opList.peek() == "+" || opList.peek() == "-" || opList.isEmpty()) {
                    newNumList.push(answer)
                }
            } else if (operator == "+" || operator == "-") {
                if (operator != "") {
                    newOpList.push(operator)
                }
                if (opList.peek() == "+" || opList.peek() == "-" || opList.isEmpty()) {
                    newNumList.push(temp1)
                }
                answer = temp1
            }
        }

        if (newOpList.size > 0) {
            skip = true
            operator = ""
            answer = 0F
            while (newOpList.isNotEmpty()) {
                try {
                    temp2 = newNumList.pop()
                } catch (E: NoSuchElementException) {
                    null
                }

                if (skip == false && newOpList.isNotEmpty()) {
                    try {
                        operator = newOpList.pop()
                    } catch (E: NoSuchElementException) {
                        null
                    }
                }

                if (operator == "+") {
                    answer = temp2 + answer
                } else if (operator == "-") {
                    answer -= temp2
                }

                if (operator == "") {
                    answer = temp2
                    skip = false
                }
            }
            txtInput.append(answer.toString())
        }

        lastNumeric = true
        newNumList.forEach { txtInput.append(it.toString()) }
        return answer
    }

    fun bedmasLogic(numList: Deque<Float>, opList: Deque<String>) {
        var temp1: Float = 0F
        var temp2: Float = 0F
        var operator = ""
        var newNumList: Deque<Float> = LinkedList()
        var newOpList: Deque<String> = LinkedList()
        var answer: Float = -1F
        var skip: Boolean = true
        /*
        Algorithim Steps:
        1: Separate number values and operators ------------------------------------[DONE]
        2: temp values equal to popped values   ------------------------------------[DONE]
        3: if/else logic reads operator --------------------------------------------[DONE]
        4: if MUL/DIV execute operation and push to newNumList ---------------------[DONE]
        5: if ADD/SUB push number to newNumList, and push operator to new stack-----[DONE]
        6: Perform addition and subtraction to get answer---------------------------[DONE]
         */
        while (opList.isNotEmpty()) {
            try {
                temp1 = numList.pop()
            } catch (E: NoSuchElementException) {
                null
            }

            if (skip == false && opList.isNotEmpty()) {
                try {
                    operator = opList.pop()
                } catch (E: NoSuchElementException) {
                    null
                }
            }

            if (operator == "") {
                if (opList.peek() == "+" || opList.peek() == "-") {
                    newNumList.push(temp1)
                }
                answer = temp1
                skip = false
            }
            if (operator == "*") {
                answer *= temp1
                if (opList.peek() == "+" || opList.peek() == "-" || opList.isEmpty()) {
                    newNumList.push(answer)
                }
            } else if (operator == "/") {
                answer = temp1 / answer
                if (opList.peek() == "+" || opList.peek() == "-" || opList.isEmpty()) {
                    newNumList.push(answer)
                }
            } else if (operator == "+" || operator == "-") {
                if (operator != "") {
                    newOpList.push(operator)
                }
                if (opList.peek() == "+" || opList.peek() == "-" || opList.isEmpty()) {
                    newNumList.push(temp1)
                }
                answer = temp1
            }
        }

        if (newOpList.size > 0) {
            skip = true
            operator = ""
            answer = 0F
            while (newOpList.isNotEmpty()) {
                try {
                    temp2 = newNumList.pop()
                } catch (E: NoSuchElementException) {
                    null
                }

                if (skip == false && newOpList.isNotEmpty()) {
                    try {
                        operator = newOpList.pop()
                    } catch (E: NoSuchElementException) {
                        null
                    }
                }

                if (operator == "+") {
                    answer = temp2 + answer
                } else if (operator == "-") {
                    answer -= temp2
                }

                if (operator == "") {
                    answer = temp2
                    skip = false
                }
            }
            txtInput.append(answer.toString())
        }
        lastNumeric = true
        newNumList.forEach { txtInput.append(it.toString()) }
    }



    //Brackets
    //Exponents

    //Division
    //Multiplication
    //Addition
    //Subtraction
}