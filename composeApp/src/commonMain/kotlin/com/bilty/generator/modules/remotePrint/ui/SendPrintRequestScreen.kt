package com.bilty.generator.modules.remotePrint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.bilty.generator.modules.remotePrint.components.CompanyBranchSelectionDialog
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

    // Dialog and current selection state
    var showSelectionDialog by remember { mutableStateOf(true) }
    var currentCompanyId by remember { mutableStateOf(companies[0].id) }
    var currentBranchId by remember { mutableStateOf(branches[0].id) }

    // Selection states for GR - default to first item
    var selectedGrId by remember { mutableStateOf(grMasterDetailsList[0].id.toString()) }

    // Set default selection for GR when data loads
    if (grMasterDetailsList.isNotEmpty()) {
        selectedGrId = grMasterDetailsList.firstOrNull()?.grInfoId.toString()
    }

    // Show dialog if company/branch not selected and data is available
    if (currentCompanyId == null && currentBranchId == null &&
        companies.isNotEmpty() && branches.isNotEmpty()
    ) {
        showSelectionDialog = true
    }


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

    // Show selection dialog
    if (showSelectionDialog && companies.isNotEmpty() && branches.isNotEmpty()) {
        CompanyBranchSelectionDialog(
            companies = companies,
            branches = branches,
            onConfirm = { companyId, branchId ->
                currentCompanyId = companyId
                currentBranchId = branchId
                showSelectionDialog = false
            }
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
                        Box(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Current Selection Display
                                item {
                                    if (currentCompanyId != null && currentBranchId != null) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            colors = androidx.compose.material3.CardDefaults.cardColors(
                                                containerColor = ThemeColors.printRequestPrimaryColor.copy(
                                                    alpha = 0.15f
                                                )
                                            )
                                        ) {
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth().padding(20.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Current Selection",
                                                        fontSize = 22.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ThemeColors.printRequestPrimaryColor
                                                    )

                                                    Button(
                                                        onClick = { showSelectionDialog = true },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = ThemeColors.printRequestPrimaryColor
                                                        )
                                                    ) {
                                                        Text("Change")
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(16.dp))

                                                // Current Company
                                                val currentCompany =
                                                    companies.find { it.id == currentCompanyId }
                                                if (currentCompany != null) {
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = androidx.compose.material3.CardDefaults.cardColors(
                                                            containerColor = Color.White
                                                        )
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(16.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = "Company",
                                                                    fontSize = 12.sp,
                                                                    color = Color.Gray,
                                                                    fontWeight = FontWeight.Medium
                                                                )
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text(
                                                                    text = currentCompany.companyName.orEmpty(),
                                                                    fontSize = 18.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = ThemeColors.printRequestPrimaryColor
                                                                )
                                                                if (!currentCompany.adminId.isNullOrEmpty()) {
                                                                    Text(
                                                                        text = "Admin: ${currentCompany.adminId}",
                                                                        fontSize = 13.sp,
                                                                        color = Color.DarkGray
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                // Current Branch
                                                val currentBranch =
                                                    branches.find { it.id == currentBranchId }
                                                if (currentBranch != null) {
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = androidx.compose.material3.CardDefaults.cardColors(
                                                            containerColor = Color.White
                                                        )
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(16.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = "Branch",
                                                                    fontSize = 12.sp,
                                                                    color = Color.Gray,
                                                                    fontWeight = FontWeight.Medium
                                                                )
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text(
                                                                    text = currentBranch.branchName.orEmpty(),
                                                                    fontSize = 18.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = ThemeColors.printRequestPrimaryColor
                                                                )
                                                                if (!currentBranch.branchCode.isNullOrEmpty()) {
                                                                    Text(
                                                                        text = "Code: ${currentBranch.branchCode}",
                                                                        fontSize = 13.sp,
                                                                        color = Color.DarkGray
                                                                    )
                                                                }
                                                                if (currentBranch.isActive) {
                                                                    Text(
                                                                        text = "● Active",
                                                                        fontSize = 12.sp,
                                                                        color = Color(0xFF388E3C)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }

                                item {
                                    Text(
                                        text = "Select Company, Branch and GR Details for printing",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Company Selection Card
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "Select Company Details",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(16.dp)
                                            )

                                            companies.forEach { company ->
                                                CompanyItem(
                                                    company = company,
                                                    isSelected = company.id == currentCompanyId,
                                                    onSelected = { currentCompanyId = company.id }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Branch Selection Card
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "Select Branch Details",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(16.dp)
                                            )

                                            branches.forEach { branch ->
                                                BranchItem(
                                                    branch = branch,
                                                    isSelected = branch.id == currentBranchId,
                                                    onSelected = { currentBranchId = branch.id }
                                                )
                                            }
                                        }
                                    }
                                }

                                // GR Selection Card
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "Select GR Details",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(16.dp)
                                            )

                                            grMasterDetailsList.forEach { grData ->
                                                GrMasterItem(
                                                    gr = grData,
                                                    isSelected = grData.grInfoId == selectedGrId,
                                                    onSelected = {
                                                        selectedGrId = grData.grInfoId.toString()
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
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