package com.lumina.app_daymood.presentation.views.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.presentation.views.auth.components.ButtonContainers
import com.lumina.app_daymood.presentation.views.auth.components.FormTextField
import com.lumina.app_daymood.presentation.views.auth.components.LoginImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState = authViewModel.uiState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Bienvenido de nuevo",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.size(160.dp)) {
            LoginImage()
        }

        Spacer(modifier = Modifier.height(32.dp))

        FormLoginView(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it }
        )

        // Mostrar error
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonContainers(
            text = "Iniciar Sesión",
            isRegister = false,
            enabled = !uiState.isLoading,  // ← Deshabilitar si está cargando
            onButtonClick = {
                authViewModel.login(
                    email = email,
                    password = password,
                    onSuccess = onLoginSuccess
                )
            },
            onNavigateClick = onNavigateToRegister
        )

        // Loading indicator
        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
fun FormLoginView(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        FormTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Correo electrónico"
        )

        FormTextField(
            value = password,
            onValueChange = onPasswordChange,
            isPassword = true,
            label = "Contraseña",
            keyboardType = KeyboardType.Password,
            isPasswordVisible = isPasswordVisible,
            onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
        )
    }
}