
package com.qubix.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    onLogin: (String, String) -> Unit = { _, _ -> },
    onSignup: (String, String) -> Unit = { _, _ -> }
) {
    var isSignup by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val blue = Color(0xFF2589FF)
    val navy = Color(0xFF080D1B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(navy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "QUBIX",
            color = blue,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            "Connect. Communicate. Share.",
            color = Color.LightGray
        )

        Spacer(Modifier.height(40.dp))

        Text(
            if (isSignup) "Create Account" else "Welcome Back",
            color = Color.White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(25.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(25.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    if (isSignup) {
                        onSignup(email, password)
                    } else {
                        onLogin(email, password)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isSignup) "Create Account" else "Login")
        }

        Spacer(Modifier.height(18.dp))

        TextButton(onClick = {
            isSignup = !isSignup
        }) {
            Text(
                if (isSignup)
                    "Already have an account? Login"
                else
                    "New here? Create an account",
                color = blue
            )
        }
    }
}
