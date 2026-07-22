package com.example.rag_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.rag_system.data.session.TokenManager
import com.example.rag_system.navigation.AppNavigation
import com.example.rag_system.ui.theme.RAG_SystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        TokenManager.init(applicationContext)
        setContent {
            RAG_SystemTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}