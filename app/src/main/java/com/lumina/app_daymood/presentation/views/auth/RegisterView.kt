package com.lumina.app_daymood.presentation.views.auth


import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.presentation.components.register.ButtonContainers
import com.lumina.app_daymood.presentation.components.register.DatePickerField
import com.lumina.app_daymood.presentation.components.register.FormTextField
import com.lumina.app_daymood.presentation.components.register.LoginImage
import com.lumina.app_daymood.presentation.viewmodels.AuthViewModel
import com.lumina.app_daymood.ui.theme.BackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {

    val uiState = authViewModel.uiState
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var birth_day by remember { mutableStateOf("") }

    var passwordMismatch by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Únete a la comunidad",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )

        Box(modifier = Modifier.size(140.dp)) {
            LoginImage()
        }

        Spacer(modifier = Modifier.height(16.dp))

        FormsView(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = { confirmPassword = it },
            birth_day = birth_day,
            onBirthDayChange = { birth_day = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (passwordMismatch) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        ButtonContainers(
            text = "Registrarse",
            isRegister = true,
            onButtonClick = {
                if (password == confirmPassword) {
                    passwordMismatch = false
                    authViewModel.register(
                        email = email,
                        password = password,
                        birth_day = birth_day,
                        onSuccess = onRegisterSuccess,
                    )
                } else {
                    passwordMismatch = true
                    Log.d("RegisterView", "Las contraseñas no coinciden")
                }
            },
            onNavigateClick = onNavigateToLogin
        )
    }
}


@Composable
fun FormsView(
    birth_day: String,
    onBirthDayChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        DatePickerField(
            value = birth_day,
            onValueChange = onBirthDayChange,
            label = "Fecha de cumpleaños"
        )

        FormTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Correo electrónico"
        )

        FormTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            isPasswordVisible = isPasswordVisible,
            onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
        )

        FormTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirmar Contraseña",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            isPasswordVisible = isConfirmPasswordVisible,
            onVisibilityChange = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
        )
    }
}