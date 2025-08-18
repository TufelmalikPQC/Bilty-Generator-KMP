package com.bilty.generator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bilty.generator.utiles.initializeApplicationContext
import com.bilty.generator.utiles.setCurrentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializeApplicationContext(this)

        setContent {
            KMPApp()
        }
    }
    override fun onResume() {
        super.onResume()
        setCurrentActivity(this)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    KMPApp()
}