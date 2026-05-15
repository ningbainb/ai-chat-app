package com.aichat.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aichat.app.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: MainViewModel, nav: NavController) {
    val items = viewModel.history.collectAsState().value
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { nav.popBackStack() }) { Text("返回") }
        LazyColumn {
            items(items) { record ->
                val parsed = viewModel.parseSuggestions(record.suggestionsJson)
                Card(modifier = Modifier.padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("风格：${record.style}")
                        Text("目标：${record.goal}")
                        Text("时间：${formatTs(record.createdAt)}")
                        Text("建议：${parsed.firstOrNull()?.text ?: "(无)"}")
                        Text("记录ID：${record.id}")
                    }
                }
            }
        }
    }
}

private fun formatTs(ts: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(ts))
