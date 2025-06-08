package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun DimensionField(
    state: MutableState<TextFieldValue>,
    label: String,
    unit: String,
    focus: FocusRequester,
    nextFocus: FocusRequester? = null,
    onDone: () -> Unit = {}
) {
    val keyboard = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = state.value,
        onValueChange = { state.value = it },
        label = { Text("$label ($unit)") },
        modifier = Modifier
            .focusRequester(focus),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = if (nextFocus != null) ImeAction.Next else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocus?.requestFocus() },
            onDone = {
                keyboard?.hide()
                onDone()
            }
        )
    )
}
