package com.superhuman.livesustainably.report

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportView(
    viewModel: ReportViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sustainability Report",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2563EB),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2563EB))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                item {
                    PeriodSelector(
                        selectedPeriod = state.selectedPeriod,
                        onPeriodSelected = { viewModel.selectPeriod(it) }
                    )
                }

                item {
                    val stats = viewModel.getCurrentStats()
                    stats?.let {
                        CO2SavingsCard(co2Saved = it.co2Saved, period = state.selectedPeriod)
                    }
                }

                item {
                    CO2ChartCard(dailyData = state.dailyData)
                }

                item {
                    TransportBreakdownCard(breakdown = state.transportBreakdown)
                }

                item {
                    val stats = viewModel.getCurrentStats()
                    stats?.let {
                        MetricsGridCard(stats = it)
                    }
                }

                item {
                    AchievementsCard(achievements = state.achievements)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedPeriod == ReportPeriod.WEEKLY,
                onClick = { onPeriodSelected(ReportPeriod.WEEKLY) },
                label = { Text("Weekly") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2563EB),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )
            FilterChip(
                selected = selectedPeriod == ReportPeriod.MONTHLY,
                onClick = { onPeriodSelected(ReportPeriod.MONTHLY) },
                label = { Text("Monthly") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2563EB),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun CO2SavingsCard(co2Saved: Double, period: ReportPeriod) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🌍",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${String.format("%.1f", co2Saved)} kg",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "CO₂ Saved This ${if (period == ReportPeriod.WEEKLY) "Week" else "Month"}",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Equivalent to ${(co2Saved / 22).toInt()} trees planted 🌳",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CO2ChartCard(dailyData: List<DailyData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Daily CO₂ Savings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (dailyData.isNotEmpty()) {
                val maxValue = dailyData.maxOfOrNull { it.co2 } ?: 10.0

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val barWidth = size.width / (dailyData.size * 2)
                    val spacing = barWidth

                    dailyData.forEachIndexed { index, data ->
                        val barHeight = (data.co2 / maxValue * size.height * 0.8).toFloat()
                        val x = index * (barWidth + spacing) + spacing / 2

                        drawRoundRect(
                            color = Color(0xFF10B981),
                            topLeft = Offset(x, size.height - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dailyData.forEach { data ->
                        Text(
                            text = data.day,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransportBreakdownCard(breakdown: List<TransportType>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Transport Usage",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f
                        breakdown.forEach { transport ->
                            val sweepAngle = 360f * transport.percentage / 100f
                            drawArc(
                                color = Color(android.graphics.Color.parseColor(transport.color)),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 24f, cap = StrokeCap.Butt),
                                size = Size(size.width * 0.9f, size.height * 0.9f),
                                topLeft = Offset(size.width * 0.05f, size.height * 0.05f)
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Text(
                        text = "🚴",
                        fontSize = 32.sp
                    )
                }

                Column {
                    breakdown.forEach { transport ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(transport.color)))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = transport.type,
                                fontSize = 14.sp,
                                color = Color(0xFF1F2937)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${transport.percentage}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricsGridCard(stats: StatsData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Key Metrics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(icon = "🚴", value = "${stats.bikeKm}", label = "Bike km")
                MetricItem(icon = "🚶", value = "${stats.walkKm}", label = "Walk km")
                MetricItem(icon = "🚌", value = "${stats.publicTransportTrips}", label = "Bus trips")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(icon = "🚗", value = "${stats.carTripsAvoided}", label = "Car avoided")
                MetricItem(icon = "🥗", value = "${stats.sustainableMeals}", label = "Eco meals")
                MetricItem(icon = "♻️", value = "${stats.recycledItems}", label = "Recycled")
            }
        }
    }
}

@Composable
fun MetricItem(icon: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 28.sp)
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
fun AchievementsCard(achievements: List<Achievement>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Achievements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                achievements.forEach { achievement ->
                    AchievementItem(achievement = achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF3C7)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (achievement.icon) {
                    "streak" -> "🔥"
                    "trophy" -> "🏆"
                    "tree" -> "🌳"
                    else -> "⭐"
                },
                fontSize = 28.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = achievement.value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        Text(
            text = achievement.title,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}
