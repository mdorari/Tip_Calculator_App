package com.example.tipcalculatorapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculatorapp.components.InputField
import com.example.tipcalculatorapp.ui.theme.TipCalculatorAppTheme
import com.example.tipcalculatorapp.util.calculateTotalPerPerson
import com.example.tipcalculatorapp.util.calculateTotalTip
import com.example.tipcalculatorapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
//                TopHeader()
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipCalculatorAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPeron: Double = 0.0) {
    Surface(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = RoundedCornerShape(12.dp)), color = Color(0xFFE9D7F7)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPeron)
            Text(text = "Total Per Person", style = MaterialTheme.typography.bodyLarge)
            Text(text = "$$total", style = MaterialTheme.typography.bodyLarge)
        }

    }
}


@Preview
@Composable
fun MainContent() {
    val splitBy = remember {
        mutableStateOf(1)
    }
    val splitRange = IntRange(start = 1, endInclusive = 20)
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm(
            splitBy = splitBy,
            splitRange = splitRange,
            totalPerPersonState = totalPerPersonState,
            tipAmountState = tipAmountState
        ) {}
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable


fun BillForm(
    modifier: Modifier = Modifier,
    splitBy: MutableState<Int>,
    splitRange: IntRange = 1..100,
    totalPerPersonState: MutableState<Double>,
    tipAmountState: MutableState<Double>,
    // val lambdaName: (InputType) -> ReturnType = { arguments:InputType -> body }
    onValueChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    //remove all whitespaces using "trim" and check if it's not empty
    //then return true. later will check if it's empty, will ignore further
    // processes. Otherwise hides keyboard.
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current


    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()



    TopHeader(totalPerPeron = totalPerPersonState.value)

    Surface(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())
                    Log.d("TAG", "total bill: ${totalBillState.value}")
                    keyboardController?.hide()
                    totalPerPersonState.value =
                        calculateTotalPerPerson(
                            totalBill = totalBillState.value.toDouble(),
                            splitBy = splitBy.value,
                            tipPercentage = tipPercentage
                        )
                }
            )
            if (validState) {
                Row(
                    modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                    Spacer(modifier = modifier.width(120.dp))
                    Row(
                        modifier = modifier.padding(horizontal = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                splitBy.value =
                                    if (splitBy.value > splitRange.first) splitBy.value - 1
                                    else 1

                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitBy.value,
                                        tipPercentage = tipPercentage
                                    )
                            })
                        Text(
                            text = "${splitBy.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitBy.value < splitRange.last) {
                                    splitBy.value += 1
                                    totalPerPersonState.value =
                                        calculateTotalPerPerson(
                                            totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitBy.value,
                                            tipPercentage = tipPercentage
                                        )
                                }
                            })
                    }
                }

                //Tip Amount Row
                Row(modifier = modifier.padding(horizontal = 6.dp, vertical = 12.dp)) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }

                //Tip percentage text and slider
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "%$tipPercentage")
                    Log.d("TAG", "tip Percentage 2: $tipPercentage")
                    Spacer(modifier = Modifier.height(14.dp))

                    //slider
                    Slider(
                        value = sliderPositionState.value,
//                    steps = 3,
//                    valueRange = 0f..0.2f,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )
                            //TODO - Check slider range and steps. It doesn't work well.
//                        Log.d("TAG", "slider Position: ${sliderPositionState.value}")
//                        Log.d("TAG", "tip Amount: ${tipAmountState.value}")
//                        Log.d("TAG", "tip Percentage: $tipPercentage")


                            totalPerPersonState.value =
                                calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitBy.value,
                                    tipPercentage = tipPercentage
                                )
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        onValueChangeFinished = {}
                    )
                }
            } else {
                Box {}
            }

        }

    }
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    TipCalculatorAppTheme {
////        TopHeader()
//        MainContent()
//    }
//}
