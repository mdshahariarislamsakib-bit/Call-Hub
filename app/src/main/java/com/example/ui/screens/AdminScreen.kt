package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ReportLogEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminScreen(
    reportLogs: List<ReportLogEntity>,
    onUpdateReportStatus: (id: Int, status: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                Icon(Icons.Default.ArrowBack, null, tint = TextPrimaryDark)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Admin & Moderation Panel", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Live Users", fontSize = 11.sp, color = TextSecondaryDark)
                    Text("12,480", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Active Calls", fontSize = 11.sp, color = TextSecondaryDark)
                    Text("3,140", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Reports", fontSize = 11.sp, color = TextSecondaryDark)
                    Text("${reportLogs.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("📋 Reported Users Queue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

        Spacer(modifier = Modifier.height(10.dp))

        if (reportLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No user reports in queue. System safe!", color = TextSecondaryDark)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reportLogs) { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_report_item_${log.id}"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = if (log.isChildSafetyEscalated) androidx.compose.foundation.BorderStroke(1.5.dp, Color.Red) else null
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Reported: ${log.reportedName}", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                if (log.isChildSafetyEscalated) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = Color.Red) {
                                        Text("⚠️ PRIORITY ESCALATED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(4.dp))
                                    }
                                } else {
                                    Surface(shape = RoundedCornerShape(8.dp), color = DarkSurfaceVariant) {
                                        Text(log.status, fontSize = 10.sp, color = GoldAccent, modifier = Modifier.padding(4.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Reason: ${log.reason}", fontSize = 12.sp, color = NeonPink, fontWeight = FontWeight.SemiBold)
                            if (log.details.isNotEmpty()) {
                                Text("Details: ${log.details}", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Text("Time: ${sdf.format(Date(log.timestamp))}", fontSize = 10.sp, color = TextSecondaryDark)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onUpdateReportStatus(log.id, "BANNED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Ban Account", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onUpdateReportStatus(log.id, "DISMISSED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Dismiss", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
