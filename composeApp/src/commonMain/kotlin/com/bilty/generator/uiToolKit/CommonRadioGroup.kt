package com.bilty.generator.uiToolKit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bilty.generator.model.enums.PrintOrientation


@Composable
fun CommonRadioGroup(
    options: List<PrintOrientation>,
    selectedValue: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        options.forEach { option ->
            FlowRow(
                itemVerticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable { onOptionSelected(option.value) }
            ) {
                RadioButton(
                    selected = selectedValue == option.value,
                    onClick = { onOptionSelected(option.value) }
                )
                Text(
                    text = option.title,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
