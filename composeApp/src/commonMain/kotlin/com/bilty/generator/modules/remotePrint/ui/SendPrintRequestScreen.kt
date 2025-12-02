package com.bilty.generator.modules.remotePrint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.title_print_request_screen
import com.bilty.generator.model.data.NotificationItem
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.modules.remotePrint.components.NotificationDrawerContent
import com.bilty.generator.theme.ThemeColors
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SendPrintRequestScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    val scope = rememberCoroutineScope()
    var showRedDot by remember { mutableStateOf(true) }
    var autoApprove by remember { mutableStateOf(true) }

    // Sample notification data
    val notifications = remember {
        listOf(
            NotificationItem("ABC Transport Co.", "GR#12345", 1710590400000L, PrintStatus.PENDING),
            NotificationItem("XYZ Logistics", "GR#12344", 1710504000000L, PrintStatus.COMPLETED),
            NotificationItem("PQR Movers", "GR#12343", 1710417600000L, PrintStatus.FAILED),
            NotificationItem("LMN Carriers", "GR#12342", 1710331200000L, PrintStatus.PRINTING),
            NotificationItem("DEF Transport", "GR#12341", 1710244800000L, PrintStatus.COMPLETED),
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(400.dp).fillMaxHeight(),
                drawerShape = RoundedCornerShape(0.dp),
            ) {
                NotificationDrawerContent(
                    notifications = notifications,
                    autoApprove = autoApprove,
                    onAutoApproveChange = { autoApprove = it },
                    onClose = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().background(ThemeColors.printRequestPrimaryColor)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.title_print_request_screen),
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Bold
                    )

                    Box(modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    scope.launch {
                                        drawerState.open()
                                        showRedDot = false
                                    }
                                }
                        )

                        if (showRedDot) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Color.Red, CircleShape)
                            )
                        }
                    }
                }

            },
            floatingActionButton = {
                Button(
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = ThemeColors.printRequestPrimaryColor
                    ),
                    modifier = Modifier,
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(text = "New Print Request")
                }
            },
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        contentAlignment = Alignment.Center
                    ) {

                    }
                }
            }
        )
    }
}



@Preview
@Composable
fun SendPrintRequestScreenPreview() {
    SendPrintRequestScreen()
}