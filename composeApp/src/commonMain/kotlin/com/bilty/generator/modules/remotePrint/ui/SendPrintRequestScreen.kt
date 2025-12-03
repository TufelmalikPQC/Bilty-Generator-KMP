package com.bilty.generator.modules.remotePrint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.title_print_request_screen
import com.bilty.generator.model.data.NotificationItem
import com.bilty.generator.model.enums.PrintStatus
import com.bilty.generator.modules.remotePrint.components.BranchItem
import com.bilty.generator.modules.remotePrint.components.CompanyItem
import com.bilty.generator.modules.remotePrint.components.GrMasterItem
import com.bilty.generator.modules.remotePrint.components.NotificationDrawerContent
import com.bilty.generator.theme.ThemeColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SendPrintRequestScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showRedDot by remember { mutableStateOf(true) }
    var autoApprove by remember { mutableStateOf(true) }
    val sendPrintRequestViewModel = SendPrintRequestViewModel()

    val companies by sendPrintRequestViewModel.companies.collectAsState()
    val branches by sendPrintRequestViewModel.branches.collectAsState()
    val grMasterDetailsList by sendPrintRequestViewModel.grMasterList.collectAsState()


    // Sample notification data
    val notifications = remember {
        mutableListOf(
            NotificationItem("ABC Transport Co.", "GR#12345", 1710590400000L, PrintStatus.PENDING),
            NotificationItem("XYZ Logistics", "GR#12344", 1710504000000L, PrintStatus.COMPLETED),
            NotificationItem("PQR Movers", "GR#12343", 1710417600000L, PrintStatus.FAILED),
            NotificationItem("LMN Carriers", "GR#12342", 1710331200000L, PrintStatus.PRINTING),
            NotificationItem("DEF Transport", "GR#12341", 1710244800000L, PrintStatus.COMPLETED),
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .background(ThemeColors.printRequestPrimaryColor)
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
                        Column(
                            modifier = Modifier.padding(it)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "Select Company, Branch and GR Details for printing.. ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .widthIn(min = 300.dp, max = 500.dp)

                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Select Company Details",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(companies) { company ->
                                            CompanyItem(company = company)
                                        }
                                    }
                                }


                                Card(
                                    modifier = Modifier
                                        .widthIn(min = 300.dp, max = 500.dp)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Select Branch Details",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(branches) { branch ->
                                            BranchItem(branch = branch)
                                        }
                                    }
                                }


                                Card(
                                    modifier = Modifier
                                        .widthIn(min = 300.dp, max = 500.dp)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Select GR Details",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(grMasterDetailsList) { grData ->
                                            GrMasterItem(gr = grData)
                                        }
                                    }
                                }
                            }

                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SendPrintRequestScreenPreview() {
    SendPrintRequestScreen(rememberNavController())
}