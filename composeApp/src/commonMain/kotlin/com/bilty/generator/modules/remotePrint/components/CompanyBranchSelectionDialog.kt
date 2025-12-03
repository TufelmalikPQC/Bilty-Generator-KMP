package com.bilty.generator.modules.remotePrint.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bilty.generator.model.data.Branch
import com.bilty.generator.model.data.Company
import com.bilty.generator.theme.ThemeColors

@Composable
fun CompanyBranchSelectionDialog(
    companies: List<Company>,
    branches: List<Branch>,
    onConfirm: (companyId: Long?, branchId: Long?) -> Unit,
    onDismiss: () -> Unit = {}
) {
    var selectedCompanyId by remember { mutableStateOf<Long?>(companies.firstOrNull()?.id) }
    var selectedBranchId by remember { mutableStateOf<Long?>(branches.firstOrNull()?.id) }

    Dialog(onDismissRequest = { /* Prevent dismissal */ }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Company and Branch",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColors.printRequestPrimaryColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please select your company and branch to continue",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Company Selection
                Text(
                    text = "Select Company",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(companies) { company ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCompanyId = company.id }
                                    .background(
                                        if (company.id == selectedCompanyId)
                                            ThemeColors.printRequestPrimaryColor.copy(alpha = 0.1f)
                                        else Color.Transparent
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = company.companyName.orEmpty(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (company.id == selectedCompanyId)
                                            ThemeColors.printRequestPrimaryColor
                                        else Color.Black
                                    )
                                    Text(
                                        text = company.adminId.orEmpty(),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Branch Selection
                Text(
                    text = "Select Branch",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(branches) { branch ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBranchId = branch.id }
                                    .background(
                                        if (branch.id == selectedBranchId)
                                            ThemeColors.printRequestPrimaryColor.copy(alpha = 0.1f)
                                        else Color.Transparent
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = branch.branchName.orEmpty(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (branch.id == selectedBranchId)
                                            ThemeColors.printRequestPrimaryColor
                                        else Color.Black
                                    )
                                    Text(
                                        text = branch.branchCode.orEmpty(),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (selectedCompanyId != null && selectedBranchId != null) {
                                onConfirm(selectedCompanyId, selectedBranchId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeColors.printRequestPrimaryColor
                        ),
                        enabled = selectedCompanyId != null && selectedBranchId != null
                    ) {
                        Text("Confirm Selection")
                    }
                }
            }
        }
    }
}