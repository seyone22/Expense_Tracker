package com.seyone22.expensetracker.ui.screen.report.composables

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer.ColumnProvider.Companion.series
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.seyone22.expensetracker.data.model.Report
import com.seyone22.expensetracker.ui.screen.report.ReportViewModel
import kotlinx.coroutines.launch

@Composable
fun ReportCard(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel,
    report: Report
) {
    val coroutineScope = rememberCoroutineScope()
    var isEditing by remember { mutableStateOf(false) }
    var sqlInput by remember(report.SQLCONTENT) { mutableStateOf(report.SQLCONTENT ?: "") }
    var templateInput by remember(report.TEMPLATECONTENT) { mutableStateOf(report.TEMPLATECONTENT ?: "chart_type=bar") }

    val queryResults = remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Reload query when SQL content or template changes
    val loadQuery = {
        coroutineScope.launch {
            try {
                queryResults.value = viewModel.runReportQuery(sqlInput)
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage ?: "Query execution failed"
                queryResults.value = null
            }
        }
    }

    LaunchedEffect(sqlInput) {
        loadQuery()
    }

    val chartType = when {
        templateInput.contains("chart_type=pie") -> "pie"
        templateInput.contains("chart_type=donut") -> "donut"
        templateInput.contains("chart_type=line") -> "line"
        else -> "bar"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.REPORTNAME ?: "Unnamed Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!report.DESCRIPTION.isNullOrBlank()) {
                        Text(
                            text = report.DESCRIPTION,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Row {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit SQL Query",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.deleteReport(report)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Report",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Editable SQL Section
            AnimatedVisibility(visible = isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "SQL Query Builder",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = sqlInput,
                        onValueChange = { sqlInput = it },
                        label = { Text("SQL Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(vertical = 8.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Chart Type",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("bar", "line", "pie", "donut").forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                RadioButton(
                                    selected = chartType == type,
                                    onClick = {
                                        templateInput = "chart_type=$type"
                                        coroutineScope.launch {
                                            viewModel.updateReport(
                                                report.copy(
                                                    SQLCONTENT = sqlInput,
                                                    TEMPLATECONTENT = "chart_type=$type"
                                                )
                                            )
                                        }
                                    }
                                )
                                Text(
                                    text = type.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.updateReport(
                                        report.copy(
                                            SQLCONTENT = sqlInput,
                                            TEMPLATECONTENT = templateInput
                                        )
                                    )
                                    isEditing = false
                                }
                            }
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Run and Save")
                            Spacer(Modifier.width(4.dp))
                            Text("Run & Save")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Render Section
            val results = queryResults.value
            if (errorMessage.value != null) {
                Text(
                    text = errorMessage.value ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            } else if (results == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (results.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data returned for this query.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                // Parse label list and series values
                val labels = results.map { row ->
                    row.entries.firstOrNull { it.value is String }?.value as? String ?: ""
                }
                val numericKeys = results.firstOrNull()?.filter { it.value is Number }?.keys?.toList() ?: emptyList()

                if (chartType == "pie" || chartType == "donut") {
                    // Custom animated Donut/Pie Chart
                    val seriesData = results.map { row ->
                        val label = row.entries.firstOrNull { it.value is String }?.value as? String ?: "Unknown"
                        val valSum = numericKeys.sumOf { (row[it] as? Number)?.toDouble() ?: 0.0 }
                        label to valSum
                    }.filter { it.second > 0 }

                    if (seriesData.isNotEmpty()) {
                        CustomDonutChart(
                            data = seriesData,
                            isDonut = chartType == "donut"
                        )
                    } else {
                        Text("No positive numerical data found for pie chart.")
                    }
                } else {
                    // Cartesian Charts (Vico column/line)
                    val labelListKey = remember { ExtraStore.Key<List<String>>() }
                    val modelProducer = remember { CartesianChartModelProducer() }

                    LaunchedEffect(results, chartType) {
                        modelProducer.runTransaction {
                            if (chartType == "line") {
                                numericKeys.forEach { key ->
                                    val values = results.map { (it[key] as? Number)?.toDouble() ?: 0.0 }
                                    lineModel { series(values) }
                                }
                            } else {
                                numericKeys.forEach { key ->
                                    val values = results.map { (it[key] as? Number)?.toDouble() ?: 0.0 }
                                    columnModel { series(values) }
                                }
                            }
                            extras { it[labelListKey] = labels }
                        }
                    }

                    val valueFormatter = CartesianValueFormatter { context, value, _ ->
                        val extraLabels = context.model.extraStore[labelListKey]
                        extraLabels?.getOrNull(value.toInt()) ?: value.toString()
                    }

                    val layer = if (chartType == "line") {
                        rememberLineCartesianLayer()
                    } else {
                        rememberColumnCartesianLayer(
                            ColumnCartesianLayer.ColumnProvider.series(
                                rememberLineComponent(
                                    thickness = 12.dp
                                )
                            )
                        )
                    }

                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            layer,
                            startAxis = VerticalAxis.rememberStart(),
                            bottomAxis = HorizontalAxis.rememberBottom(
                                valueFormatter = valueFormatter,
                                itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() }
                            )
                        ),
                        modelProducer = modelProducer,
                        scrollState = rememberVicoScrollState(),
                        zoomState = rememberVicoZoomState(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Query Results Data Table
                Text(
                    text = "Query Results",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        // Table Headers
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
                            results.firstOrNull()?.keys?.forEach { header ->
                                Text(
                                    text = header,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .padding(end = 8.dp)
                                )
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        // Table Rows
                        results.forEachIndexed { idx, row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (idx % 2 == 0) MaterialTheme.colorScheme.surface
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                    .padding(vertical = 4.dp)
                            ) {
                                row.values.forEach { cell ->
                                    val cellText = when (cell) {
                                        is Double -> String.format("%.2f", cell)
                                        is Float -> String.format("%.2f", cell)
                                        else -> cell.toString()
                                    }
                                    Text(
                                        text = cellText,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier
                                            .width(120.dp)
                                            .padding(end = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDonutChart(
    data: List<Pair<String, Double>>,
    isDonut: Boolean = true
) {
    val total = data.sumOf { it.second }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val chartColors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFFEF4444), // Rose
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFF06B6D4), // Cyan
        Color(0xFF84CC16)  // Lime
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                data.forEachIndexed { index, pair ->
                    val sweepAngle = ((pair.second / total) * 360f).toFloat() * animatedProgress.value
                    if (isDonut) {
                        drawArc(
                            color = chartColors[index % chartColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 24.dp.toPx())
                        )
                    } else {
                        drawArc(
                            color = chartColors[index % chartColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                    }
                    startAngle += sweepAngle
                }
            }

            if (isDonut) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%.2f", total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom flowing legend
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    rowItems.forEachIndexed { idx, pair ->
                        val index = data.indexOf(pair)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(chartColors[index % chartColors.size], CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${pair.first}: ${String.format("%.1f", (pair.second / total) * 100)}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
