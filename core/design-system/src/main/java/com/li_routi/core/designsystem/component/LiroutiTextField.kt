package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

@Composable
fun LiroutiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Placeholder Text",
    labelText: String = "Label",
    helperText: String = "Helper Text",
    showLabel: Boolean = true,
    showHelper: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (showLabel) {
            Text(
                text = labelText,
                style = LiroutiTheme.typography.body2LongRegular.copy(fontWeight = FontWeight.Bold),
                color = LiroutiTheme.colors.labelDefault,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
                .border(1.dp, LiroutiTheme.colors.borderDefault, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = LiroutiTheme.typography.body2LongRegular.copy(color = LiroutiTheme.colors.labelDefault),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (showHelper) {
            Text(
                text = helperText,
                style = LiroutiTheme.typography.captionRegular,
                color = LiroutiTheme.colors.labelSub,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiTextFieldPreview() {
    LiroutiFrontendTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LiroutiTextField(value = "", onValueChange = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiTextFieldBarePreview() {
    LiroutiFrontendTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LiroutiTextField(
                value = "",
                onValueChange = {},
                showLabel = false,
                showHelper = false,
            )
        }
    }
}
