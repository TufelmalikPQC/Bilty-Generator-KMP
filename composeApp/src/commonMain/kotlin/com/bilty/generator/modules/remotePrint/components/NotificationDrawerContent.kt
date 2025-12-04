package com.bilty.generator.modules.remotePrint.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import biltygenerator.composeapp.generated.resources.Res
import biltygenerator.composeapp.generated.resources.cd_close
import biltygenerator.composeapp.generated.resources.label_notifications
import biltygenerator.composeapp.generated.resources.message_auto_approve_print_request
import biltygenerator.composeapp.generated.resources.message_no_new_print_requests
import com.bilty.generator.model.data.NotificationItem
import com.bilty.generator.theme.ThemeColors
import org.jetbrains.compose.resources.stringResource


@Composable
fun NotificationDrawerContent(
    notifications: List<NotificationItem>,
    autoApprove: Boolean,
    onAutoApproveChange: (Boolean) -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onClose: () -> Unit
) {
    println("📋 NotificationDrawerContent: Composing with ${notifications.size} notifications")
    notifications.forEachIndexed { index, item ->
        println("📋 NotificationDrawerContent: [$index] GR=${item.grNo}, Company=${item.companyName}, Status=${item.status}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Drawer Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ThemeColors.printRequestPrimaryColor)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.label_notifications),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(
                onClick = onClose
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(Res.string.cd_close),
                    tint = Color.White
                )
            }
        }

        // Auto Approve Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAutoApproveChange(!autoApprove) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = autoApprove,
                onCheckedChange = onAutoApproveChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = ThemeColors.printRequestPrimaryColor,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.message_auto_approve_print_request),
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.LightGray
        )

        // Notifications List
        if (notifications.isEmpty()) {
            println("📋 NotificationDrawerContent: Rendering EMPTY state")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.message_no_new_print_requests),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            println("📋 NotificationDrawerContent: Rendering LazyColumn with ${notifications.size} items")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = notifications,
                    key = { "${it.grNo}_${it.timestamp}" }
                ) { notification ->
                    println("📋 NotificationDrawerContent: Rendering item GR=${notification.grNo}")
                    NotificationItem(
                        notification = notification,
                        showApprovalButtons = !autoApprove,
                        onApprove = {
                            println("✅ NotificationDrawerContent: Approve clicked for GR=${notification.grNo}")
                            onApprove(notification.grNo)
                        },
                        onReject = {
                            println("❌ NotificationDrawerContent: Reject clicked for GR=${notification.grNo}")
                            onReject(notification.grNo)
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

