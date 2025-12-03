package com.bilty.generator.modules.remotePrint.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bilty.generator.model.data.Company
import io.ktor.util.date.getTimeMillis
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CompanyItem(company: Company) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {

        // ===== TOP SECTION (logo + name + admin) =====
        Row(verticalAlignment = Alignment.CenterVertically) {

            AsyncImage(
                model = company.companyLogo,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = company.companyName.orEmpty(),
                    style = MaterialTheme.typography.titleMedium
                )

                if (company.isDefault == true) {
                    Text(
                        text = "Default Company",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = company.adminId.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


        // ===== DETAILS WITH LABELS =====

        LabelValue("ID", company.id)
        LabelValue("Registration Date", company.registrationDate)
        LabelValue("Created At", company.createdAt)
        LabelValue("Updated At", company.updatedAt)

        // Only isActive is forced to have a label
        Text(
            text = "Active: ${company.isActive}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun LabelValue(label: String, value: Any?) {
    if (value == null) return
    if (value.toString().isBlank()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun CompanyItemPreview() {
    val companies = listOf(
        Company(
            id = 1,
            companyName = "Alpha Traders",
            companyLogo = "https://picsum.photos/200",
            registrationDate = getTimeMillis(),
            adminId = "admin_1",
            isActive = true,
            isDefault = true,
            createdAt = getTimeMillis(),
            updatedAt = getTimeMillis()
        ),
        Company(
            id = 2,
            companyName = "Beta Logistics",
            companyLogo = "https://picsum.photos/200",
            registrationDate = getTimeMillis(),
            adminId = "admin_2",
            isActive = true,
            isDefault = false,
            createdAt = getTimeMillis(),
            updatedAt = getTimeMillis()
        ),
        Company(
            id = 3,
            companyName = "Gamma Industries",
            companyLogo = "https://picsum.photos/200",
            registrationDate = getTimeMillis(),
            adminId = "admin_3",
            isActive = true,
            isDefault = false,
            createdAt = getTimeMillis(),
            updatedAt = getTimeMillis()
        )
    )
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items = companies) { company ->
            CompanyItem(company = company)
        }
    }
}