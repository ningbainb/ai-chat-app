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

@Composable
fun HistoryScreen(viewModel: MainViewModel, nav: NavController) {
    val items = viewModel.history.collectAsState().value
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { nav.popBackStack() }) { Text("返回") }
        LazyColumn {
            items(items) {
                Card(modifier = Modifier.padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("风格：${it.style}")
                        Text("目标：${it.goal}")
                        Text("记录ID：${it.id}")
                    }
                }
            }
        }
    }
}
