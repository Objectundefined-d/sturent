package com.example.flat_rent_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.flat_rent_app.presentation.navigation.AppNav
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            FlatrentappTheme {
                AppNav()
            }
        }
    }
}
