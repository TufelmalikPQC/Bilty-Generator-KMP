package com.bilty.generator.uiToolKit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreviewTabs(
    htmlContentWithImage: @Composable () -> Unit,
    htmlContentWithoutImage: @Composable () -> Unit
) {
    // 1. A state to hold the index of the currently selected tab. 0 for the first, 1 for the second.
    var selectedTabIndex by remember { mutableStateOf(0) }

    // 2. A list of the titles for your tabs.
    val tabTitles = listOf("With Image", "Content Only")

    Column {
        // 3. The TabRow container.
        TabRow(selectedTabIndex = selectedTabIndex) {
            // 4. Loop through your titles to create a Tab for each one.
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index, // Highlights the tab if it's selected
                    onClick = { selectedTabIndex = index }, // Updates the state when a tab is clicked
                    text = { Text(text = title) } // The text to display in the tab
                )
            }
        }

        // 5. (Optional) Display content based on the selected tab.
        // This is where you would show your different previews.
        when (selectedTabIndex) {
            0 -> {
               htmlContentWithImage()
            }
            1 -> {
               htmlContentWithoutImage()
            }
        }
    }
}