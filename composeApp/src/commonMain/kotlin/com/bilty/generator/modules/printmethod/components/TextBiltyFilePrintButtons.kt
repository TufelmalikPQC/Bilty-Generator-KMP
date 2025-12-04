package com.bilty.generator.modules.printmethod.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.button_use_direct_print
import biltygenerator.composeapp.generated.resources.button_use_pdf_print
import biltygenerator.composeapp.generated.resources.cd_direct_print
import biltygenerator.composeapp.generated.resources.cd_pdf_print
import biltygenerator.composeapp.generated.resources.label_convert_pdf_print
import biltygenerator.composeapp.generated.resources.label_direct_text_print
import biltygenerator.composeapp.generated.resources.str_direct_print_description
import biltygenerator.composeapp.generated.resources.str_pdf_print_description
import org.jetbrains.compose.resources.stringResource


@Composable
fun TextBiltyFilePrintButtons(
    isButtonsDisabled: Boolean = true,
    onDirectPrint: () -> Unit,
    onPdfPrint: () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Direct Text Print Button
        ElevatedCard(
            modifier = Modifier.weight(1f)
                .padding(bottom = 16.dp)
                .alpha(if (isButtonsDisabled) 1f else 0.4f),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = stringResource(Res.string.cd_direct_print),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(Res.string.label_direct_text_print),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(Res.string.str_direct_print_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        onDirectPrint()

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.button_use_direct_print))
                }
            }
        }


        // Convert to PDF then Print Button
        ElevatedCard(
            modifier = Modifier.weight(1f)
                .alpha(if (isButtonsDisabled) 1f else 0.4f),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Article,
                    contentDescription = stringResource(Res.string.cd_pdf_print),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = stringResource(Res.string.label_convert_pdf_print),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(Res.string.str_pdf_print_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        onPdfPrint()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(stringResource(Res.string.button_use_pdf_print))
                }
            }
        }
    }
}