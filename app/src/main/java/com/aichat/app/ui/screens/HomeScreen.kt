package com.aichat.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aichat.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, nav: NavController) {
    var uri by remember { mutableStateOf<Uri?>(null) }
    var goal by remember { mutableStateOf("自然回复，不要油腻") }
    var style by remember { mutableStateOf("自然") }
    val loading by viewModel.loading.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val error by viewModel.error.collectAsState()
    val clipboard = LocalClipboardManager.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { selected -> uri = selected }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { launcher.launch("image/*") }) { Text(if (uri == null) "选择截图" else "重新选择") }
            Button(onClick = { nav.navigate("settings") }) { Text("设置") }
            Button(onClick = { nav.navigate("history") }) { Text("历史") }
        }
        Text(if (uri == null) "未选择图片" else "已选择图片：$uri")
        OutlinedTextField(value = style, onValueChange = { style = it }, label = { Text("风格") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = goal, onValueChange = { goal = it }, label = { Text("目标") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { style = "自然"; goal = "自然回复，不要油腻" }, enabled = !loading) { Text("重置推荐") }
            Button(onClick = { viewModel.generate(uri, goal.trim(), style.trim()) }, enabled = !loading && uri != null) { Text("生成回复") }
        }

        if (loading) CircularProgressIndicator()
        if (!error.isNullOrBlank()) Text("错误：$error")

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestions) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${item.style}：${item.text}")
                        Button(onClick = { clipboard.setText(AnnotatedString(item.text)) }) { Text("复制") }
                    }
                }
            }
        }
    }
}
