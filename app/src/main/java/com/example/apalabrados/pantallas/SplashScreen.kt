package com.example.apalabrados.pantallas

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apalabrados.navegacion.PantallasApp
import com.example.apalabrados.viewModel.ViewModel
import kotlinx.coroutines.delay
import com.example.apalabrados.R
import com.example.apalabrados.ui.theme.AzulFondo

@Composable
fun SplashScreen(navController: NavController, ViewModel: ViewModel){
    LaunchedEffect(key1 = true){
        delay(3000)
        navController.popBackStack()
        navController.navigate(PantallasApp.Inicio.route)
    }

    Splash()
}

@Composable
fun Splash(){
    Column (

        modifier = androidx.compose.ui.Modifier.fillMaxSize().background(color = AzulFondo),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
            ){

        Image(painter = painterResource(id =R.drawable.preguntadoss ) , contentDescription = null, modifier = Modifier.size(400.dp))
    }
}

