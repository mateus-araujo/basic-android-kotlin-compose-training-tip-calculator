package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tiptime.ui.theme.TipTimeTheme
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    TipTimeScreen()
                }
            }
        }
    }
}

@Composable
fun EditNumberField(
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    value: String
) {
    TextField(
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        label = { Text(stringResource(label)) },
        modifier = modifier.fillMaxWidth(),
        onValueChange = onValueChange,
        singleLine = true,
        value = value,
    )
}

@Composable
fun RoundTheTipRow(
    modifier: Modifier = Modifier,
    onRoundUpChange: (Boolean) -> Unit,
    roundUp: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.round_up_tip))
        Switch(
            colors = SwitchDefaults.colors(
              uncheckedThumbColor = Color.DarkGray,
            ),
            checked = roundUp,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            onCheckedChange = onRoundUpChange
        )
    }
}

@VisibleForTesting
internal fun calculateTip(
    amount: Double,
    roundUp: Boolean,
    tipPercent: Double,
): String {
    var tip = tipPercent / 100 * amount
    if (roundUp)
        tip = kotlin.math.ceil(tip)
    return NumberFormat.getCurrencyInstance().format(tip)
}

@Composable
fun TipTimeScreen() {
    var amountInput by remember { mutableStateOf("") }
    var tipInput by remember { mutableStateOf("") }
    var roundUp by remember { mutableStateOf(false) }

    val amount = amountInput.toDoubleOrNull() ?: 0.0;
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0;
    val tip = calculateTip(amount, roundUp, tipPercent);

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        EditNumberField(
            keyboardActions = KeyboardActions(
              onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Down) },
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            label = R.string.bill_amount,
            onValueChange = { amountInput = it },
            value = amountInput
        )
        EditNumberField(
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() },
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            label = R.string.how_was_the_service,
            onValueChange = { tipInput = it },
            value = tipInput
        )
        RoundTheTipRow(
            onRoundUpChange = { roundUp = it },
            roundUp = roundUp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.tip_amount, tip),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipTimeTheme {
        TipTimeScreen()
    }
}