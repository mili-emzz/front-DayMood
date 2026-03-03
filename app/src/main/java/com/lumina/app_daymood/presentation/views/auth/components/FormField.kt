package com.lumina.app_daymood.presentation.components.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lumina.app_daymood.ui.theme.MainColor

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit, //cuando el usuario escriba, notifica el nuevo valor q se actualiza
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {} //afectar icono de visibilidad
) {

    val visualTransformation =
        if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label
            )
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MainColor,
            focusedBorderColor = MainColor,
            unfocusedLabelColor = Color.Gray,
            focusedLabelColor = MainColor,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { onVisibilityChange() }) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        }
    )

    Spacer(modifier = Modifier.height(8.dp))
}