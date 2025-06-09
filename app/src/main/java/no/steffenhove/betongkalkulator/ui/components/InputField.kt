package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun InputField(
    label: String,
    state: MutableState<TextFieldValue>,
    unit: String = "",
    keyboardType: KeyboardType = KeyboardType.Number,
    imeAction: ImeAction = ImeAction.Next,
    focus: FocusRequester? = null,
    nextFocus: FocusRequester? = null,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = state.value,
        onValueChange = { state.value = it },
        label = { Text(label) },
        trailingIcon = { if (unit.isNotBlank()) Text(unit) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (focus != null) Modifier.focusRequester(focus) else Modifier),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { nextFocus?.requestFocus() },
            onDone = {
                onDone?.invoke()
            }
        )
    )
}
