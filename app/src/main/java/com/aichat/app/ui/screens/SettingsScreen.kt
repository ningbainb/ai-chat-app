package com.aichat.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aichat.app.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel, nav: NavController) {
    val settings = viewModel.readSettings()
    val baseUrl = remember { mutableStateOf(settings.first) }
    val apiKey = remember { mutableStateOf(settings.second) }
    val model = remember { mutableStateOf(settings.third) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(baseUrl.value, { baseUrl.value = it }, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(apiKey.value, { apiKey.value = it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(model.value, { model.value = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
        if (!error.isNullOrBlank()) Text("错误：$error")
        Button(onClick = {
            error = viewModel.saveSettings(baseUrl.value.trim(), apiKey.value.trim(), model.value.trim())
            if (error == null) nav.popBackStack()
        }) { Text("保存") }
    }
}
